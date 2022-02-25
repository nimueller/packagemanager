package de.salocin.packagemanager.device

import de.salocin.packagemanager.ProgressObserver
import java.nio.file.Path

interface App {

    val name: String

    val type: AppType

    val paths: List<DevicePath<Device>>

    suspend fun refreshPaths(observer: ProgressObserver? = null)

    suspend fun download(observer: ProgressObserver? = null): AppBundle

    suspend fun downloadAsSingleFile(path: Path, observer: ProgressObserver? = null)
}
