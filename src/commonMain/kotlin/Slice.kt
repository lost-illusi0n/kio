package dev.sitar.kio

public interface Sliceable {
    public operator fun get(range: IntRange): Slice

    public fun fullSlice(): Slice
}

public data class Slice(public val bytes: ByteArray, public val start: Int, public val length: Int) : Iterable<Byte> {
    init {
        require(start >= 0 && start + length <= bytes.size) { "Slice must be within range of the array (0..${bytes.size})!" }
    }

    override fun iterator(): Iterator<Byte> {
        return SliceIterator(this)
    }

    public operator fun set(index: Int, value: Byte) {
        bytes[start + index] = value
    }

    public operator fun get(index: Int): Byte {
        return bytes[start + index]
    }

    public fun copyInto(slice: Slice) {
        bytes.copyInto(slice.bytes, slice.start, start, start + length)
    }

    public companion object {
        public val Empty: Slice = Slice(ByteArray(0), 0, 0)
    }
}

public class SliceIterator(public val slice: Slice) : Iterator<Byte> {
    private var pos = 0

    override fun hasNext(): Boolean {
        return pos < slice.length
    }

    override fun next(): Byte {
        return slice[pos++]
    }
}

public operator fun ByteArray.get(range: IntRange): Slice {
    if (range.first < 0 || range.last > size) throw IndexOutOfBoundsException("Range $range is out of bounds for length $size")

    return Slice(this, range.first, range.last - range.first)
}

public fun ByteArray.fullSlice(): Slice {
    return this[0..size]
}