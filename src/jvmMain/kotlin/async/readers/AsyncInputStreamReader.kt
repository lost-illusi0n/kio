package dev.sitar.kio.async.readers

import dev.sitar.kio.Pool
import dev.sitar.kio.buffers.Buffer
import dev.sitar.kio.buffers.DefaultBufferPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.SocketException

public class AsyncInputStreamReader(
    public val inputStream: InputStream,
    override val bufferPool: Pool<Buffer> = DefaultBufferPool
) : AsyncReader {
    override var openForRead: Boolean = true
        private set

    override suspend fun discard(n: Int): Int {
        if (!openForRead) return -1

        return withContext(Dispatchers.IO) {
            inputStream.skip(n.toLong())
        }.toInt()
    }

    override suspend fun readBytes(n: Int, dest: Buffer): Int {
        if (!openForRead) return -1

        val (b, off, len) = dest[dest.writeIndex..(dest.writeIndex + n)]

        val read = try {
            inputStream.readNBytes(b, off, len)
        } catch (e: IOException) {
            openForRead = false
            return -1
        }

        if (read == -1) {
            openForRead = false
            return -1
        }

        dest.writeIndex += read
        return read
    }
}

public fun InputStream.toAsyncReader(): AsyncInputStreamReader {
    return AsyncInputStreamReader(this)
}
