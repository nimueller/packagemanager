package de.salocin.android.device

import de.salocin.android.adb.Adb
import de.salocin.android.io.ZipFile
import de.salocin.android.io.createTemporaryDirectory
import de.salocin.android.io.filename
import de.salocin.android.io.zip
import de.salocin.packagemanager.ProgressObserver
import de.salocin.packagemanager.configuration.Configuration
import de.salocin.packagemanager.device.App
import de.salocin.packagemanager.device.DevicePath
import de.salocin.packagemanager.io.TemporaryDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

data class AndroidApp(
    val device: AndroidDevice,
    override val name: String,
    override val type: AndroidAppType
) : App {

    override var paths: List<DevicePath> = emptyList()
        private set

    override suspend fun refreshPaths(observer: ProgressObserver?) {
        paths = Adb.packagePaths(device, name)
    }

    override suspend fun download(observer: ProgressObserver?): AndroidAppBundle {
        observer?.notifyMessageChange("Refreshing installation paths")
        refreshPaths(observer)
        observer?.notifyProgressChange(0)
        observer?.notifyMaxProgressChange(paths.size)

        val appFiles = mutableListOf<AndroidAppFile>()
        val directory = Configuration.workingDirectory.resolve(APPS_DIRECTORY).resolve(name)
        directory.createDirectory()

        createTemporaryDirectory { temporaryDirectory ->
            paths.forEachIndexed { index, devicePath ->
                downloadSingleApk(observer, devicePath, temporaryDirectory, index)
            }

            moveDownloadedFiles(observer, temporaryDirectory, directory, appFiles)
        }

        return AndroidAppBundle(directory, appFiles)
    }

    override suspend fun downloadAsSingleFile(path: Path, observer: ProgressObserver?) {
        val bundle = download()
        bundle.path.listDirectoryEntries().zip(ZipFile(path))
    }

    private suspend fun downloadSingleApk(
        observer: ProgressObserver?,
        devicePath: DevicePath,
        temporaryDirectory: TemporaryDirectory,
        index: Int
    ): Path {
        observer?.notifyMessageChange("Downloading ${devicePath.path.filename}")
        val path = temporaryDirectory.path.resolve(devicePath.path.filename)
        Adb.pull(device, devicePath, path)
        observer?.notifyProgressChange(index + 1)
        return path
    }

    private suspend fun moveDownloadedFiles(
        observer: ProgressObserver?,
        temporaryDirectory: TemporaryDirectory,
        destinationDirectory: Path,
        appFiles: MutableList<AndroidAppFile>
    ) {
        observer?.notifyMessageChange("Moving downloaded files")
        observer?.notifyProgressChange(0)

        withContext(Dispatchers.IO) {
            temporaryDirectory.path.listDirectoryEntries().forEachIndexed { index, path ->
                val dest = destinationDirectory.resolve(path.name)
                val file = AndroidAppFile(dest)
                appFiles += file
                path.copyTo(dest)
                observer?.notifyProgressChange(index + 1)
            }
        }
    }

    override fun toString(): String {
        return name
    }

    companion object {

        const val APPS_DIRECTORY = "apps"
    }
}
