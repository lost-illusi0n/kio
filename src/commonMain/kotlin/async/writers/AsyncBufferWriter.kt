package dev.sitar.kio.async.writers

import dev.sitar.kio.ByteOrder
import dev.sitar.kio.Pool
import dev.sitar.kio.Slice
import dev.sitar.kio.buffers.Buffer
import dev.sitar.kio.buffers.DefaultBufferPool
import dev.sitar.kio.buffers.buffer

public class AsyncBufferWriter(
    public val buffer: Buffer = buffer(),
    override val bufferPool: Pool<Buffer> = DefaultBufferPool
) : AsyncWriter {
    override val openForWrite: Boolean
        get() = true

    override suspend fun writeBytes(slice: Slice): Int {
        buffer.writeBytes(slice)
        return slice.length
    }

    override suspend fun write(byte: Byte) {
        buffer.write(byte)
    }

    override suspend fun writeChar(char: Char, order: ByteOrder) {
        buffer.writeChar(char, order)
    }

    override suspend fun writeShort(short: Short, order: ByteOrder) {
        buffer.writeShort(short, order)
    }

    override suspend fun writeInt(int: Int, order: ByteOrder) {
        buffer.writeInt(int, order)
    }

    override suspend fun writeLong(long: Long, order: ByteOrder) {
        buffer.writeLong(long, order)
    }
}

public fun Buffer.toAsyncWriter(): AsyncBufferWriter {
    return AsyncBufferWriter(this)
}