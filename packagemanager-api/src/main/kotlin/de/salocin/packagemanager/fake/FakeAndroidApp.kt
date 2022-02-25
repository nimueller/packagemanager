package de.salocin.packagemanager.fake

import de.salocin.packagemanager.ProgressObserver
import de.salocin.packagemanager.device.*
import java.nio.file.Path

class FakeAndroidApp(override val name: String) : App {

    override val type: AppType = FakeAppType

    override val paths: List<DevicePath<Device>> = emptyList()

    override suspend fun refreshPaths(observer: ProgressObserver?) {
        // nothing to do
    }

    override suspend fun download(observer: ProgressObserver?): AppBundle {
        return FakeAppBundle
    }

    override suspend fun downloadAsSingleFile(path: Path, observer: ProgressObserver?) {
        // nothing to do
    }
}
