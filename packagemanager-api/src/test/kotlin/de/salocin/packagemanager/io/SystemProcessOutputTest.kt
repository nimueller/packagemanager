package de.salocin.packagemanager.io

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import java.io.InputStream
import java.io.PrintStream
import kotlin.test.Test

class SystemProcessOutputTest {
    private lateinit var inputStream: InputStream
    private lateinit var outputParser: OutputParser<String>
    private lateinit var outputPipe: PrintStream
    private lateinit var output: SystemProcessOutput<String>

    @BeforeEach
    fun setUp() {
        inputStream = mockk()
        outputParser = mockk()
        outputPipe = mockk()
        output = SystemProcessOutput(inputStream, outputParser, outputPipe)

        every { outputPipe.print(any<Char>()) } returns Unit
    }

    @Test
    fun `readAvailable should return empty list when no bytes are available`() {
        // given
        every { inputStream.available() } returns 0

        // when
        val result = output.readAvailable()

        // then
        assert(result.isEmpty())
    }

    @Test
    fun `readAvailable should process available bytes correctly`() {
        // given
        mockLineBytesToParse()

        // when
        val result = output.readAvailable()

        // then
        assertEquals(listOf("parsedLine1", "parsedLine2"), result)
    }

    @Test
    fun `readAvailable with multiple read operations should process available bytes correctly`() {
        // given
        val testBytes1 = "line1\nline2\n".toByteArray()
        val testBytes2 = "line3\nline4\n".toByteArray()
        every { inputStream.available() } returns testBytes1.size andThen testBytes2.size andThen 0
        every { inputStream.readNBytes(any()) } returns testBytes1 andThen testBytes2
        every { outputParser.parseLine("line1") } returns "parsedLine1"
        every { outputParser.parseLine("line2") } returns "parsedLine2"
        every { outputParser.parseLine("line3") } returns "parsedLine3"
        every { outputParser.parseLine("line4") } returns "parsedLine4"

        // when
        val result = output.readAvailable()

        // then
        assertEquals(listOf("parsedLine1", "parsedLine2", "parsedLine3", "parsedLine4"), result)
    }

    @Test
    fun `readRemaining should return parsed lines after end of stream`() {
        // given
        mockLineBytesToParse()
        val testBytes = "line1\nline2\n".toByteArray()
        every { inputStream.readAllBytes() } returns testBytes

        // when
        val result = output.readRemaining()

        // then
        assertEquals(listOf("parsedLine1", "parsedLine2"), result)
    }

    @Test
    fun `outputPipe should receive correct output`() {
        // given
        mockLineBytesToParse()

        // when
        output.readAvailable()

        // then
        verify { outputPipe.print('l') }
        verify { outputPipe.print('i') }
        verify { outputPipe.print('n') }
        verify { outputPipe.print('e') }
        verify { outputPipe.print('1') }
        verify { outputPipe.print('\n') }
        verify { outputPipe.print('l') }
        verify { outputPipe.print('i') }
        verify { outputPipe.print('n') }
        verify { outputPipe.print('e') }
        verify { outputPipe.print('2') }
        verify { outputPipe.print('\n') }
    }

    @Test
    fun `null outputPipe should successfully parse`() {
        // given
        output = SystemProcessOutput(inputStream, outputParser, null)
        mockLineBytesToParse()

        // when
        val result = output.readAvailable()

        // then
        assertEquals(listOf("parsedLine1", "parsedLine2"), result)
    }

    @Test
    fun `null outputParser should output empty list`() {
        // given
        output = SystemProcessOutput(inputStream, null, outputPipe)
        mockLineBytesToParse()

        // when
        val result = output.readAvailable()

        // then
        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `null outputParser should still pipe`() {
        // given
        output = SystemProcessOutput(inputStream, null, outputPipe)
        mockLineBytesToParse()

        // when
        output.readAvailable()

        // then
        verify { outputPipe.print('l') }
        verify { outputPipe.print('i') }
        verify { outputPipe.print('n') }
        verify { outputPipe.print('e') }
        verify { outputPipe.print('1') }
        verify { outputPipe.print('\n') }
        verify { outputPipe.print('l') }
        verify { outputPipe.print('i') }
        verify { outputPipe.print('n') }
        verify { outputPipe.print('e') }
        verify { outputPipe.print('2') }
        verify { outputPipe.print('\n') }
    }

    private fun mockLineBytesToParse() {
        val testBytes = "line1\nline2\n".toByteArray()
        every { inputStream.available() } returns testBytes.size andThen 0
        every { inputStream.readNBytes(any()) } returns testBytes andThen byteArrayOf()
        every { outputParser.parseLine("line1") } returns "parsedLine1"
        every { outputParser.parseLine("line2") } returns "parsedLine2"
    }
}
