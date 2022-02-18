package de.salocin.packagemanager.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.PrintStream

open class SystemProcess<T>(
    private val arguments: List<String>,
    private val stdoutParser: OutputParser<T>? = null,
    private val stderrParser: OutputParser<T>? = null,
    private val commandPipe: PrintStream? = null,
    private val stdoutPipe: PrintStream? = null,
    private val stderrPipe: PrintStream? = null
) : Process<T> {

    override suspend fun execute(): List<T> {
        return runInterruptible(Dispatchers.IO) {
            val parsedLines = mutableListOf<T>()
            commandPipe?.println(arguments.joinToString(" "))

            val process = ProcessBuilder(arguments).start()!!
            val stdout = SystemProcessOutput(process.inputStream, stdoutParser, stdoutPipe)
            val stderr = SystemProcessOutput(process.errorStream, stderrParser, stderrPipe)

            while (process.isAlive) {
                parsedLines += stdout.tryRead()
                parsedLines += stderr.tryRead()
            }

            parsedLines += stdout.readRemaining()
            parsedLines += stderr.readRemaining()

            return@runInterruptible parsedLines
        }
    }

    companion object {

        fun build(arguments: List<String>): SystemProcess<String> {
            return SystemProcess(arguments)
        }

        fun <T> build(arguments: List<String>, stdoutParser: OutputParser<T>): SystemProcess<T> {
            return SystemProcess(arguments, stdoutParser)
        }
    }
}
