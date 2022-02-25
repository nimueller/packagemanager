package de.salocin.android.device

import de.salocin.android.adb.Adb
import de.salocin.packagemanager.ProgressObserver
import de.salocin.packagemanager.device.Device
import java.nio.file.Path

data class AndroidDevice(override val model: String, override val serialNumber: String) : Device {

    override var apps: List<AndroidApp> = emptyList()
        private set

    private val rootPaths: List<AndroidDevicePath> = listOf(AndroidDevicePath(this, "."))

    override suspend fun refreshRootPaths(observer: ProgressObserver?): List<AndroidDevicePath> {
        return rootPaths
    }

    override suspend fun refreshApps(observer: ProgressObserver?) {
        apps = Adb.packages(this)
    }

    override suspend fun installApp(path: Path, observer: ProgressObserver?) {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return model
    }
}
