package de.salocin.packagemanager.device

import de.salocin.packagemanager.ProgressObserver
import java.nio.file.Path

interface AppFile {

    val path: Path

    suspend fun decompile(observer: ProgressObserver? = null): DecompiledApp
}
