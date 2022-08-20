package dev.sitar.kio.buffers

import dev.sitar.kio.Pool
import dev.sitar.kio.Slice
import dev.sitar.kio.Sliceable
import dev.sitar.kio.emptyPool

public interface Buffer : SequentialReader, SequentialWriter, AbsoluteReader, AbsoluteWriter, Sliceable {
    override val bufferPool: Pool<Buffer>

    public val capacity: Int

    public fun resize(n: Int)

    public override fun ensureSequentialCapacity(n: Int) {
        return ensureCapacity(writeIndex, n)
    }

    public override fun ensureCapacity(index: Int, n: Int) {
        val minCapacity = index + n
        if (minCapacity > capacity) {
            var newCapacity = capacity shl 1
            if (newCapacity < minCapacity) newCapacity = minCapacity

            resize(newCapacity)
        }
    }

    override fun discard(n: Int) {
        readIndex += n
    }

    override fun readBytes(n: Int, dest: Buffer) {
        getBytes(readIndex, n, dest).also { readIndex += n }
    }

    override fun writeBytes(slice: Slice) {
        return setBytes(writeIndex, slice).also { writeIndex += slice.length }
    }

    public fun close() {}

    override fun fullSlice(): Slice {
        return get(0..writeIndex)
    }

    public fun toByteArray(): ByteArray

    public companion object {
        public val Empty: Buffer = object : Buffer {
            override val bufferPool: Pool<Buffer> = emptyPool { error("Cannot generate buffers for an empty buffer!") }

            override val capacity: Int = 0

            override var readIndex: Int
                get() = 0
                set(_) {}

            override var writeIndex: Int
                get() = 0
                set(_) {}

            override fun resize(n: Int) {
                throw IllegalStateException("Can't resize an empty buffer!")
            }

            override fun getBytes(index: Int, n: Int, dest: Buffer) {
                throw IllegalStateException("Can't read an empty buffer!")
            }

            override fun setBytes(index: Int, slice: Slice) {
                throw IllegalStateException("Can't write an empty buffer!")
            }

            override fun get(range: IntRange): Slice {
                return Slice.Empty
            }

            override fun toByteArray(): ByteArray = ByteArray(0)
        }
    }
}

public fun Buffer.contentEquals(b: Buffer): Boolean {
    if (this.capacity != b.capacity) return false
    var index = 0
    while (index < capacity && this[index] == b[index]) index++
    return index == capacity
}

public fun buffer(size: Int = 32, block: Buffer.() -> Unit = { }): Buffer {
    return ByteArrayBuffer(ByteArray(size)).apply(block)
}