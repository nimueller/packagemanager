package de.salocin.android.device

import de.salocin.packagemanager.device.AppBundle
import de.salocin.packagemanager.device.AppFile
import java.nio.file.Path

data class AndroidAppBundle(override val path: Path, override val appFiles: List<AppFile>) : AppBundle
