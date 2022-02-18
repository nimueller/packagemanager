package de.salocin.packagemanager.configuration

import java.nio.file.Path

object Configuration {

    val workingDirectory: Path = Path.of(System.getProperty("user.dir"))
}
