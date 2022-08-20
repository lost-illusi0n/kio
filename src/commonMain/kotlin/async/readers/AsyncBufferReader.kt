package dev.sitar.kio.async.readers

import dev.sitar.kio.ByteOrder
import dev.sitar.kio.Pool
import dev.sitar.kio.buffers.Buffer
import dev.sitar.kio.buffers.DefaultBufferPool
import kotlin.math.min

public class AsyncBufferReader(
    public val buffer: Buffer,
    override val bufferPool: Pool<Buffer> = DefaultBufferPool
) : AsyncReader {
    override val openForRead: Boolean
        get() = buffer.readIndex < buffer.writeIndex

    override suspend fun discard(n: Int): Int {
        if (openForRead) return -1
        val count = min(n, buffer.capacity - buffer.readIndex)
        buffer.readIndex += count
        return count
    }

    override suspend fun readBytes(n: Int, dest: Buffer): Int {
        if (!openForRead) return -1
        val count = min(n, buffer.capacity - buffer.readIndex)
        buffer.readBytes(count, dest)
        return count
    }

    override suspend fun read(): Byte {
        return buffer.read()
    }

    override suspend fun readChar(order: ByteOrder): Char {
        return buffer.readChar(order)
    }

    override suspend fun readShort(order: ByteOrder): Short {
        return buffer.readShort(order)
    }

    override suspend fun readInt(order: ByteOrder): Int {
        return buffer.readInt(order)
    }

    override suspend fun readLong(order: ByteOrder): Long {
        return buffer.readLong(order)
    }
}

public fun Buffer.toAsyncReader(): AsyncBufferReader {
    return AsyncBufferReader(this)
}