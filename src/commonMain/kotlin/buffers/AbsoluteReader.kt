package dev.sitar.kio.buffers

import dev.sitar.kio.ByteOrder
import dev.sitar.kio.Pool
import dev.sitar.kio.use

public interface AbsoluteReader {
    public val bufferPool: Pool<Buffer>

    public fun getBytes(index: Int, n: Int, dest: Buffer)

    public operator fun get(index: Int): Byte {
        return internalRead(index, Byte.SIZE_BYTES) { this[0..1][0] }
    }

    public fun getChar(index: Int, order: ByteOrder = ByteOrder.Default): Char {
        return internalRead(index, Char.SIZE_BYTES) { order.constructCharOrdered(this[0], this[1]) }
    }

    public fun getShort(index: Int, order: ByteOrder = ByteOrder.Default): Short {
        return internalRead(index, Short.SIZE_BYTES) { order.constructShortOrdered(this[0], this[1]) }
    }

    public fun getInt(index: Int, order: ByteOrder = ByteOrder.Default): Int {
        return internalRead(index, Int.SIZE_BYTES) { order.constructIntOrdered(this[0], this[1], this[2], this[3]) }
    }

    public fun getLong(index: Int, order: ByteOrder = ByteOrder.Default): Long {
        return internalRead(index, Long.SIZE_BYTES) {
            order.constructLongOrdered(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7]
            )
        }
    }
}

public fun AbsoluteReader.getBytes(index: Int, n: Int): Buffer {
    return buffer(n).also { getBytes(index, n, it) }
}

private inline fun <R> AbsoluteReader.internalRead(index: Int, n: Int, block: Buffer.() -> R): R {
    return bufferPool.use(n) {
        getBytes(index, n, it)
        block(it)
    }
}