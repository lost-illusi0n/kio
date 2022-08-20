package dev.sitar.kio.buffers

import dev.sitar.kio.ByteOrder
import dev.sitar.kio.Pool
import dev.sitar.kio.use

public interface SequentialReader {
    public val bufferPool: Pool<Buffer>

    public var readIndex: Int

    public fun discard(n: Int)

    public fun readBytes(n: Int, dest: Buffer)

    public fun read(): Byte {
        return internalRead(1) { this[0] }
    }

    public fun readChar(order: ByteOrder = ByteOrder.Default): Char {
        return internalRead(2) {
            order.constructCharOrdered(this[0], this[1])
        }
    }

    public fun readShort(order: ByteOrder = ByteOrder.Default): Short {
        return internalRead(2) {
            order.constructShortOrdered(this[0], this[1])
        }
    }

    public fun readInt(order: ByteOrder = ByteOrder.Default): Int {
        return internalRead(4) {
            order.constructIntOrdered(this[0], this[1], this[2], this[3])
        }
    }

    public fun readLong(order: ByteOrder = ByteOrder.Default): Long {
        return internalRead(8) {
            order.constructLongOrdered(this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7])
        }
    }
}

public fun SequentialReader.readBytes(n: Int): Buffer {
    return buffer(n).also { readBytes(n, it) }
}

private inline fun <R> SequentialReader.internalRead(n: Int, block: Buffer.() -> R): R {
    return bufferPool.use(n) {
        readBytes(n, it)
        block(it)
    }
}