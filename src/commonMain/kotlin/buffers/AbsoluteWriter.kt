package dev.sitar.kio.buffers

import dev.sitar.kio.ByteOrder
import dev.sitar.kio.Pool
import dev.sitar.kio.Slice
import dev.sitar.kio.fullSlice
import dev.sitar.kio.use

public interface AbsoluteWriter {
    public val bufferPool: Pool<Buffer>
        get() = DefaultBufferPool

    public fun ensureCapacity(index: Int, n: Int)

    public fun setBytes(index: Int, slice: Slice)

    public operator fun set(index: Int, b: Byte) {
        internalWrite(index, Byte.SIZE_BYTES) {
            this[0..1][0] = b
        }
    }

    public fun setChar(index: Int, char: Char, order: ByteOrder = ByteOrder.Default) {
        internalWrite(index, Char.SIZE_BYTES) { order.deconstructCharOrdered(char, this) }
    }

    public fun setShort(index: Int, short: Short, order: ByteOrder = ByteOrder.Default) {
        internalWrite(index, Short.SIZE_BYTES) { order.deconstructShortOrdered(short, this) }
    }

    public fun setInt(index: Int, int: Int, order: ByteOrder = ByteOrder.Default) {
        internalWrite(index, Int.SIZE_BYTES) { order.deconstructIntOrdered(int, this) }
    }

    public fun setLong(index: Int, long: Long, order: ByteOrder = ByteOrder.Default) {
        internalWrite(index, Long.SIZE_BYTES) { order.deconstructLongOrdered(long, this) }
    }
}

public fun AbsoluteWriter.setBytes(index: Int, bytes: ByteArray) {
    setBytes(index, bytes.fullSlice())
}

private fun AbsoluteWriter.internalWrite(index: Int, n: Int, block: Buffer.() -> Unit) {
    bufferPool.use(n) {
        ensureCapacity(index, n)
        block(it)
        setBytes(index, it[0..n])
    }
}