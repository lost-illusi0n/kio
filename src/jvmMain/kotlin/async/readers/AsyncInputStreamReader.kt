package dev.sitar.kio.async.readers

import dev.sitar.kio.Pool
import dev.sitar.kio.buffers.Buffer
import dev.sitar.kio.buffers.DefaultBufferPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

public class AsyncInputStreamReader(
    public val inputStream: InputStream,
    override val bufferPool: Pool<Buffer> = DefaultBufferPool
) : AsyncReader {
    override val openForRead: Boolean
        get() = true

    override suspend fun discard(n: Int): Int {
        if (!openForRead) return -1

        return withContext(Dispatchers.IO) {
            inputStream.skip(n.toLong())
        }.toInt()
    }

    override suspend fun readBytes(n: Int, dest: Buffer): Int {
        if (!openForRead) return -1

        val (b, off, len) = dest[dest.writeIndex..(dest.writeIndex + n)]

        val read = withContext(Dispatchers.IO) {
            inputStream.readNBytes(b, off, len)
        }
        dest.writeIndex += read
        return read
    }
}

public fun InputStream.toAsyncReader(): AsyncInputStreamReader {
    return AsyncInputStreamReader(this)
}
