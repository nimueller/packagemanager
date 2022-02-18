package de.salocin.packagemanager.io

import java.io.InputStream
import java.io.PrintStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * A output from a system process from the [inputStream], which might be piped to a print stream with [outputPipe].
 * The output lines are parsed according to the supplied [outputParser], if available.
 */
class SystemProcessOutput<T>(
    private val inputStream: InputStream,
    private val outputParser: OutputParser<T>?,
    private val outputPipe: PrintStream?
) {

    private val buffer = ByteBuffer.allocate(BUFFER_SIZE)

    /**
     * Tries to read the next available bytes, without blocking the current thread.
     * It will not wait for more bytes if no bytes are available.
     */
    fun tryRead(): List<T> {
        val list = mutableListOf<T>()

        while (inputStream.available() > 0) {
            val bytes = inputStream.readNBytes(maxOf(inputStream.available(), BUFFER_SIZE))
            read(bytes, list)
        }

        return list
    }

    /**
     * Reads all remaining bytes from the process output. This method will block until an end of stream is reached.
     */
    fun readRemaining(): List<T> {
        val list = mutableListOf<T>()
        read(inputStream.readAllBytes(), list)
        return list
    }

    private fun read(bytes: ByteArray, parsedLines: MutableList<T>) {
        for (byte in bytes) {
            outputPipe?.print(byte.toInt().toChar())

            if (outputParser != null) {
                if (byte.toInt() == '\n'.code) {
                    finishLine(parsedLines, outputParser)
                } else if (buffer.position() < buffer.limit()) {
                    buffer.put(byte)
                }
            }
        }
    }

    private fun finishLine(list: MutableList<T>, parser: OutputParser<T>) {
        val bytes = ByteArray(buffer.position())
        val endIndex = if (isWindowsLineFeed) buffer.position() - 1 else buffer.position()

        for (i in 0 until endIndex) {
            bytes[i] = buffer[i]
        }

        val line = String(bytes, StandardCharsets.UTF_8).replace(0.toChar().toString(), "")
        parser.parseLine(line)?.let { parsedLine -> list += parsedLine }
        buffer.position(0)
    }

    companion object {

        private const val BUFFER_SIZE = 8 * 1024

        private val isWindowsLineFeed = System.lineSeparator() == "\r\n"
    }
}
