package de.salocin.android.device

import de.salocin.packagemanager.ProgressObserver
import de.salocin.packagemanager.configuration.Configuration
import de.salocin.packagemanager.device.App
import de.salocin.packagemanager.device.AppBundle
import de.salocin.packagemanager.device.AppType
import de.salocin.packagemanager.device.DevicePath
import java.nio.file.Path

class FakeAndroidApp(override val name: String) : App {

    override val type: AppType = AndroidAppType.FAKE

    override val paths: List<DevicePath> = emptyList()

    override suspend fun refreshPaths(observer: ProgressObserver?) {
        // nothing to do
    }

    override suspend fun download(observer: ProgressObserver?): AppBundle {
        return AndroidAppBundle(Configuration.workingDirectory, emptyList())
    }

    override suspend fun downloadAsSingleFile(path: Path, observer: ProgressObserver?) {
        // nothing to do
    }
}
