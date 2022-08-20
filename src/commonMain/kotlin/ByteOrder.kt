package dev.sitar.kio

import dev.sitar.kio.buffers.Buffer
import kotlin.experimental.or

public enum class ByteOrder {
    LittleEndian {
        // deconstructors
        override fun deconstructShortOrdered(short: Short, buffer: Buffer) {
            buffer.write(short.toByte())
            buffer.write((short.toInt() shr 8).toByte())
        }

        override fun deconstructIntOrdered(int: Int, buffer: Buffer) {
            buffer.write((int.toByte()))
            buffer.write(((int shr 8).toByte()))
            buffer.write(((int shr 16).toByte()))
            buffer.write(((int shr 24).toByte()))
        }

        override fun deconstructLongOrdered(long: Long, buffer: Buffer) {
            buffer.write((long.toByte()))
            buffer.write(((long shr 8).toByte()))
            buffer.write(((long shr 16).toByte()))
            buffer.write(((long shr 24).toByte()))
            buffer.write(((long shr 32).toByte()))
            buffer.write(((long shr 40).toByte()))
            buffer.write(((long shr 48).toByte()))
            buffer.write(((long shr 56).toByte()))
        }

        // constructors
        override fun constructShortOrdered(b0: Byte, b1: Byte): Short {
            return constructShort(b1, b0)
        }

        override fun constructIntOrdered(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int {
            return constructInt(b3, b2, b1, b0)
        }

        override fun constructLongOrdered(
            b0: Byte,
            b1: Byte,
            b2: Byte,
            b3: Byte,
            b4: Byte,
            b5: Byte,
            b6: Byte,
            b7: Byte
        ): Long {
            return constructLong(b7, b6, b5, b4, b3, b2, b1, b0)
        }
    },
    BigEndian {
        // deconstructors
        override fun deconstructShortOrdered(short: Short, buffer: Buffer) {
            buffer.write((short.toInt() shr 8).toByte())
            buffer.write(short.toByte())
        }

        override fun deconstructIntOrdered(int: Int, buffer: Buffer) {
            buffer.write(((int shr 24).toByte()))
            buffer.write(((int shr 16).toByte()))
            buffer.write(((int shr 8).toByte()))
            buffer.write((int.toByte()))
        }

        override fun deconstructLongOrdered(long: Long, buffer: Buffer) {
            buffer.write(((long shr 56).toByte()))
            buffer.write(((long shr 48).toByte()))
            buffer.write(((long shr 40).toByte()))
            buffer.write(((long shr 32).toByte()))
            buffer.write(((long shr 24).toByte()))
            buffer.write(((long shr 16).toByte()))
            buffer.write(((long shr 8).toByte()))
            buffer.write((long.toByte()))
        }

        // constructors
        override fun constructShortOrdered(b0: Byte, b1: Byte): Short {
            return constructShort(b0, b1)
        }

        override fun constructIntOrdered(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int {
            return constructInt(b0, b1, b2, b3)
        }

        override fun constructLongOrdered(
            b0: Byte,
            b1: Byte,
            b2: Byte,
            b3: Byte,
            b4: Byte,
            b5: Byte,
            b6: Byte,
            b7: Byte
        ): Long {
            return constructLong(b0, b1, b2, b3, b4, b5, b6, b7)
        }
    };

    // deconstructors
    public fun deconstructCharOrdered(char: Char, buffer: Buffer) {
        deconstructShortOrdered(char.code.toShort(), buffer)
    }

    public abstract fun deconstructShortOrdered(short: Short, buffer: Buffer)
    public abstract fun deconstructIntOrdered(int: Int, buffer: Buffer)
    public abstract fun deconstructLongOrdered(long: Long, buffer: Buffer)

    // constructors
    public fun constructCharOrdered(b0: Byte, b1: Byte): Char {
        return constructShortOrdered(b0, b1).toInt().toChar()
    }

    public abstract fun constructShortOrdered(b0: Byte, b1: Byte): Short
    public abstract fun constructIntOrdered(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int
    public abstract fun constructLongOrdered(
        b0: Byte,
        b1: Byte,
        b2: Byte,
        b3: Byte,
        b4: Byte,
        b5: Byte,
        b6: Byte,
        b7: Byte
    ): Long

    public companion object {
        public val Default: ByteOrder = BigEndian
    }
}

// all constructs are in big endian
private fun constructShort(b0: Byte, b1: Byte): Short {
    return (b0.toInt() and 0xFF shl 8).toShort() or (b1.toInt() and 0xFF).toShort()
}

private fun constructInt(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int {
    return (b0.toInt() and 0xFF shl 24) or
            (b1.toInt() and 0xFF shl 16) or
            (b2.toInt() and 0xFF shl 8) or
            (b3.toInt() and 0xFF)
}

private fun constructLong(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6: Byte, b7: Byte): Long {
    return (b0.toLong() and 0xFF shl 56) or
            (b1.toLong() and 0xFF shl 48) or
            (b2.toLong() and 0xFF shl 40) or
            (b3.toLong() and 0xFF shl 32) or
            (b4.toLong() and 0xFF shl 24) or
            (b5.toLong() and 0xFF shl 16) or
            (b6.toLong() and 0xFF shl 8) or
            (b7.toLong() and 0xFF)
}