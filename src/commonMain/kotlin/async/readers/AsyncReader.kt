package dev.sitar.kio.async.readers

import dev.sitar.kio.ByteOrder
import dev.sitar.kio.Pool
import dev.sitar.kio.async.writers.AsyncWriter
import dev.sitar.kio.buffers.Buffer
import dev.sitar.kio.buffers.ByteArrayBuffer
import dev.sitar.kio.buffers.buffer
import dev.sitar.kio.use
import kotlin.math.min

public interface AsyncReader {
    public val bufferPool: Pool<Buffer>

    public val openForRead: Boolean

    public operator fun iterator(): AsyncReaderIterator = AsyncReaderIterator(this)

    public suspend fun discard(n: Int): Int

    public suspend fun readBytes(n: Int, dest: Buffer): Int

    public suspend fun read(): Byte {
        return internalRead(1) {
            this[0]
        }
    }

    public suspend fun readChar(order: ByteOrder = ByteOrder.Default): Char {
        return internalRead(2) {
            order.constructCharOrdered(this[0], this[1])
        }
    }

    public suspend fun readShort(order: ByteOrder = ByteOrder.Default): Short {
        return internalRead(2) {
            order.constructShortOrdered(this[0], this[1])
        }
    }

    public suspend fun readInt(order: ByteOrder = ByteOrder.Default): Int {
        return internalRead(4) {
            order.constructIntOrdered(this[0], this[1], this[2], this[3])
        }
    }

    public suspend fun readLong(order: ByteOrder = ByteOrder.Default): Long {
        return internalRead(8) {
            order.constructLongOrdered(this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7])
        }
    }
}

public class AsyncReaderIterator(private val asyncReader: AsyncReader) {
    public operator fun hasNext(): Boolean {
        return asyncReader.openForRead
    }

    public suspend operator fun next(): Byte {
        return asyncReader.read()
    }
}

public suspend fun AsyncReader.readAllBytes(): Buffer {
    return readBytes(Int.MAX_VALUE)
}

public suspend fun AsyncReader.readBytes(len: Int): Buffer {
    require(len > 0) { "n must be greater than 0" }

    var bufs: MutableList<ByteArray>? = null
    var result: ByteArray? = null
    var total = 0
    var remaining = len
    var n: Int

    do {
        val buf = ByteArrayBuffer(ByteArray(min(remaining, 8192)))
        var nread = 0

        // read to EOF which may read more or less than buffer size
        while (
            readBytes(
                min(buf.capacity - nread, remaining), buf
            ).also { n = it } > 0
        ) {
            nread += n
            remaining -= n
        }

        if (nread > 0) {
            if (Int.MAX_VALUE - 8 - total < nread) {
                throw Exception("Required array size too large")
            }
        }
        total += nread
        if (result == null) {
            result = buf.buffer
        } else {
            if (bufs == null) {
                bufs = ArrayList()
                bufs.add(result)
            }
            bufs.add(buf.buffer)
        }
        // if the last call to read returned -1 or the number of bytes
        // requested have been read then break
    } while (n >= 0 && remaining > 0)

    if (bufs == null) {
        if (result == null) {
            return buffer(0)
        }

        if (result.size != total) result = result.copyOf(total)

        return ByteArrayBuffer(result).also { it.writeIndex = total }
    }

    result = ByteArray(total)
    var offset = 0
    remaining = total
    for (b in bufs) {
        val count: Int = min(b.size, remaining)
        b.copyInto(result, offset, count)
        offset += count
        remaining -= count
    }

    return ByteArrayBuffer(result).also { it.writeIndex = total }
}

public suspend fun AsyncReader.readFully(n: Int) {
    return readFully(n, buffer(n))
}

public suspend fun AsyncReader.readFully(n: Int, dest: Buffer) {
    var remaining = n

    while (openForRead && remaining != 0) {
        val read = readBytes(n, dest)

        if (read == 0) throw Exception("Not enough data to read!")

        remaining -= read
    }

    if (remaining != 0) throw Exception("Expected $remaining more bytes but reached end of input!")
}

public suspend fun AsyncReader.pipeTo(asyncWriter: AsyncWriter) {
    val buf = ByteArrayBuffer(ByteArray(8192))

    while (openForRead) {
        readBytes(8192, buf)
        asyncWriter.writeBytes(buf.fullSlice())

        buf.writeIndex = 0
    }
}

private suspend inline fun <R> AsyncReader.internalRead(n: Int, block: Buffer.() -> R): R {
    return bufferPool.use(n) {
        readFully(n, it)
        block(it)
    }
}
