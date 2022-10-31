package dev.sitar.kio.async.writers

import dev.sitar.kio.Pool
import dev.sitar.kio.Slice
import dev.sitar.kio.async.readers.AsyncReader
import dev.sitar.kio.buffers.Buffer
import dev.sitar.kio.buffers.DefaultBufferPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

public class AsyncOutputStreamWriter(
    public val outputStream: OutputStream,
    override val bufferPool: Pool<Buffer> = DefaultBufferPool
) : AsyncWriter {
    override val openForWrite: Boolean
        get() = true

    override suspend fun writeBytes(slice: Slice): Int {
        val (b, off, len) = slice

        outputStream.write(b,  off, len)

        return len
    }
}

public fun OutputStream.toAsyncWriter(): AsyncOutputStreamWriter {
    return AsyncOutputStreamWriter(this)
}
