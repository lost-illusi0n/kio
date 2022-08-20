package dev.sitar.kio.async.writers

import dev.sitar.kio.ByteOrder
import dev.sitar.kio.Pool
import dev.sitar.kio.Slice
import dev.sitar.kio.buffers.Buffer
import dev.sitar.kio.fullSlice
import dev.sitar.kio.use

public interface AsyncWriter {
    public val bufferPool: Pool<Buffer>

    public val openForWrite: Boolean

    public suspend fun writeBytes(slice: Slice): Int

    public suspend fun write(byte: Byte) {
        internalWrite(Byte.SIZE_BYTES) { write(byte) }
    }

    public suspend fun writeChar(char: Char, order: ByteOrder = ByteOrder.Default) {
        internalWrite(Char.SIZE_BYTES) { order.deconstructCharOrdered(char, this) }
    }

    public suspend fun writeShort(short: Short, order: ByteOrder = ByteOrder.Default) {
        internalWrite(Short.SIZE_BYTES) { order.deconstructShortOrdered(short, this) }
    }

    public suspend fun writeInt(int: Int, order: ByteOrder = ByteOrder.Default) {
        internalWrite(Int.SIZE_BYTES) { order.deconstructIntOrdered(int, this) }
    }

    public suspend fun writeLong(long: Long, order: ByteOrder = ByteOrder.Default) {
        internalWrite(Long.SIZE_BYTES) { order.deconstructLongOrdered(long, this) }
    }
}

public suspend fun AsyncWriter.writeBytes(bytes: ByteArray): Int {
    return writeBytes(bytes.fullSlice())
}

public suspend fun AsyncWriter.writeFully(slice: Slice) {
    var remaining = slice.length

    with(slice) {
        while (openForWrite && remaining > 0) {
            val written = writeBytes(slice.copy(start = start + (length - remaining), length = remaining))
            if (written == 0) throw Exception("Could not write any more bytes!")
            remaining -= written
        }
    }
}

public suspend fun AsyncWriter.writeFully(bytes: ByteArray) {
    return writeFully(bytes.fullSlice())
}

private suspend fun AsyncWriter.internalWrite(n: Int, block: Buffer.() -> Unit) {
    bufferPool.use(n) {
        block(it)
        writeFully(it[0..n])
    }
}