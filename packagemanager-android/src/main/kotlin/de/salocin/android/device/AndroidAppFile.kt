package de.salocin.android.device

import de.salocin.packagemanager.ProgressObserver
import de.salocin.packagemanager.device.AppFile
import de.salocin.packagemanager.device.DecompiledApp
import java.nio.file.Path

class AndroidAppFile(override val path: Path) : AppFile {

    override suspend fun decompile(observer: ProgressObserver?): DecompiledApp {
        TODO()
    }
}
