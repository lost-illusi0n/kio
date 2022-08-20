package dev.sitar.kio

import dev.sitar.kio.async.readers.pipeTo
import dev.sitar.kio.async.readers.toAsyncReader
import dev.sitar.kio.async.writers.toAsyncWriter
import dev.sitar.kio.buffers.asBuffer
import dev.sitar.kio.buffers.buffer
import dev.sitar.kio.buffers.contentEquals
import dev.sitar.kio.buffers.readBytes
import dev.sitar.kio.buffers.writeBytes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KioTest {
    @Test
    fun buffers() {
        val data = byteArrayOf(5, 5, 5)

        buffer(4) {
            write(0xF)
            writeChar('a')
            writeShort(69)
            writeInt(420)
            writeBytes(data)
            writeLong(1337)

            assertEquals(read(), 0xF.toByte())
            assertEquals(readChar(), 'a')
            assertEquals(readShort(), 69.toShort())
            assertEquals(readInt(), 420)

            val readData = readBytes(3)
            assertTrue(
                readData.contentEquals(data.asBuffer()),
                "Expected: ${data.joinToString(", ")}\n" +
                        "Read: ${readData.fullSlice().joinToString(", ")}"
            )
            assertEquals(readLong(), 1337L)

            write(0xF)
            writeChar('a', order = ByteOrder.LittleEndian)
            writeShort(69, order = ByteOrder.LittleEndian)
            writeInt(420, order = ByteOrder.LittleEndian)
            writeLong(1337, order = ByteOrder.LittleEndian)

            assertEquals(read(), 0xF.toByte())
            assertEquals(readChar(order = ByteOrder.LittleEndian), 'a')
            assertEquals(readShort(order = ByteOrder.LittleEndian), 69.toShort())
            assertEquals(readInt(order = ByteOrder.LittleEndian), 420)
            assertEquals(readLong(order = ByteOrder.LittleEndian), 1337L)
        }
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun async() = runTest {
        val source = buffer(4) {
            writeInt(123)
            writeShort(2)
        }.toAsyncReader()

        assertEquals(source.readInt(), 123)

        val sink = buffer(4).toAsyncWriter()

        source.pipeTo(sink)

        assertEquals(sink.buffer.readShort(), 2)
    }
}