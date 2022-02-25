package de.salocin.android.io

import de.salocin.packagemanager.device.FileType
import de.salocin.packagemanager.fake.mapEachMatch
import de.salocin.packagemanager.io.RegexOutputParser
import de.salocin.packagemanager.io.TemporaryDirectory
import kotlinx.coroutines.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.isDirectory

private val lsRegex =
    Regex("^(.)(.{9})\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+(.{10}\\s.{5})\\s+([^\\s]+)")

data class LsRegexResult(
    val type: String,
    val permissions: String,
    val linkCount: String,
    val owner: String,
    val group: String,
    val size: String,
    val timestamp: String,
    val name: String
)

val LS_OUTPUT_PARSER = RegexOutputParser(lsRegex).takeAllGroups().mapEachMatch<LsRegexResult>()

@Throws(IOException::class)
fun Path.deleteRecursive() {
    Files.list(this).forEach { child ->
        if (child.isDirectory()) {
            child.deleteRecursive()
        } else {
            child.deleteExisting()
        }
    }

    deleteExisting()
}

val String.filename: String
    get() {
        return Regex("^.*/(.+)$").matchEntire(this)?.groups?.get(1)?.value ?: this
    }

fun String.parseAsFileType(): FileType? {
    return when (this) {
        "d" -> FileType.Directory
        "l" -> FileType.Link
        "-" -> FileType.Regular
        else -> null
    }
}

suspend inline fun createTemporaryDirectory(crossinline block: suspend (TemporaryDirectory) -> Unit) {
    coroutineScope {
        val createDirectoryJob: Deferred<Path> = async(Dispatchers.IO) {
            Files.createTempDirectory(null)
        }

        val directory = TemporaryDirectory(createDirectoryJob.await())

        try {
            block(directory)
        } finally {
            withContext(NonCancellable) {
                delay(500L)

                launch(Dispatchers.IO) {
                    directory.path.deleteRecursive()
                }
            }
        }
    }
}
