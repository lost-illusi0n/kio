package dev.sitar.kio.buffers

import dev.sitar.kio.Pool
import dev.sitar.kio.Slice
import dev.sitar.kio.get

public class ByteArrayBuffer(
    buffer: ByteArray,
    override val bufferPool: Pool<Buffer> = DefaultBufferPool
) : Buffer {
    public var buffer: ByteArray = buffer
        private set

    override val backingArray: ByteArray
        get() = buffer

    override val capacity: Int
        get() = buffer.size

    override var readIndex: Int = 0
        set(value) {
            if (value > buffer.size) throw IndexOutOfBoundsException("Index $value is out of bounds for length ${buffer.size}")
            field = value
        }

    override var writeIndex: Int = 0
        set(value) {
            if (value > buffer.size) throw IndexOutOfBoundsException("Index $value is out of bounds for length ${buffer.size}")
            field = value
        }

    override fun resize(n: Int) {
        buffer = buffer.copyOf(n)
    }

    override fun getBytes(index: Int, n: Int, dest: Buffer) {
        dest.writeBytes(buffer[index..(index + n)])
    }

    override fun get(index: Int): Byte {
        return buffer[index]
    }

    override fun setBytes(index: Int, slice: Slice) {
        ensureCapacity(index, slice.length)
        slice.copyInto(buffer[index..index + slice.length])
    }

    override fun set(index: Int, b: Byte) {
        buffer[index] = b
    }

    override fun write(byte: Byte) {
        ensureSequentialCapacity(1)
        this[writeIndex++] = byte
    }

    override fun get(range: IntRange): Slice {
        if (range.first < 0 || range.last > buffer.size) throw IndexOutOfBoundsException("Range $range is out of bounds for length ${buffer.size}")
        return Slice(buffer, range.first, range.last - range.first)
    }

    override fun toByteArray(): ByteArray {
        return buffer.copyOf(writeIndex)
    }
}

public fun ByteArray.asBuffer(writeIndex: Int = size): ByteArrayBuffer {
    val buffer = ByteArrayBuffer(this)
    buffer.writeIndex = writeIndex
    return buffer
}