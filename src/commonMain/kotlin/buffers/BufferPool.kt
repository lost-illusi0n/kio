package dev.sitar.kio.buffers

import dev.sitar.kio.Pool

public class SizedBufferPool(capacity: Int, chainedCapacity: Int) : Pool<Buffer>(
    capacity,
    chainedCapacity
) {
    override val Buffer.key: Int
        get() = capacity

    override fun generate(key: Int): Buffer {
        return buffer(key)
    }

    override fun cleanse(t: Buffer) {
        with(t) {
            writeIndex = 0

            repeat(capacity) {
                this[it] = 0
            }

            writeIndex = 0
            readIndex = 0
        }
    }
}

public val DefaultBufferPool: SizedBufferPool = SizedBufferPool(
    capacity = 2000,
    chainedCapacity = 10
)