package de.salocin.packagemanager.device

import de.salocin.packagemanager.ProgressObserver
import java.nio.file.Path

interface Device {

    val model: String

    val serialNumber: String

    val apps: List<App>

    suspend fun refreshRootPaths(observer: ProgressObserver? = null): List<DevicePath<Device>>

    suspend fun refreshApps(observer: ProgressObserver? = null)

    suspend fun installApp(path: Path, observer: ProgressObserver? = null)
}
