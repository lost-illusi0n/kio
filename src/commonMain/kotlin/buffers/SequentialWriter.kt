package dev.sitar.kio.buffers

import dev.sitar.kio.ByteOrder
import dev.sitar.kio.Pool
import dev.sitar.kio.Slice
import dev.sitar.kio.fullSlice
import dev.sitar.kio.use

public interface SequentialWriter {
    public val bufferPool: Pool<Buffer>

    public var writeIndex: Int

    public fun ensureSequentialCapacity(n: Int)

    public fun writeBytes(slice: Slice)

    public fun write(byte: Byte) {
        internalWrite(Byte.SIZE_BYTES) {
            this[0] = byte
        }
    }

    public fun writeChar(char: Char, order: ByteOrder = ByteOrder.Default) {
        internalWrite(Char.SIZE_BYTES) { order.deconstructCharOrdered(char, this) }
    }

    public fun writeShort(short: Short, order: ByteOrder = ByteOrder.Default) {
        internalWrite(Short.SIZE_BYTES) { order.deconstructShortOrdered(short, this) }
    }

    public fun writeInt(int: Int, order: ByteOrder = ByteOrder.Default) {
        internalWrite(Int.SIZE_BYTES) { order.deconstructIntOrdered(int, this) }
    }

    public fun writeLong(long: Long, order: ByteOrder = ByteOrder.Default) {
        internalWrite(Long.SIZE_BYTES) { order.deconstructLongOrdered(long, this) }
    }
}

public fun SequentialWriter.writeBytes(bytes: ByteArray) {
    writeBytes(bytes.fullSlice())
}

private fun SequentialWriter.internalWrite(n: Int, block: Buffer.() -> Unit) {
    bufferPool.use(n) {
        ensureSequentialCapacity(n)
        block(it)
        writeBytes(it[0..n])
    }
}