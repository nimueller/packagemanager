package de.salocin.android.io

import de.salocin.packagemanager.io.TemporaryDirectory
import kotlinx.coroutines.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.isDirectory

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
        return Regex("^.*/(.+)\$").matchEntire(this)?.groups?.get(1)?.value ?: this
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
