package de.salocin.packagemanager.device

import java.nio.file.Path

interface AppBundle {

    val path: Path

    val appFiles: List<AppFile>
}
