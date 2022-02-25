package de.salocin.packagemanager.fake

import de.salocin.packagemanager.configuration.Configuration
import de.salocin.packagemanager.device.AppBundle
import de.salocin.packagemanager.device.AppFile
import java.nio.file.Path

object FakeAppBundle : AppBundle {

    override val path: Path
        get() = Configuration.workingDirectory

    override val appFiles: List<AppFile> = emptyList()
}
