package de.salocin.android.adb

import de.salocin.android.device.AndroidApp
import de.salocin.android.device.AndroidAppType
import de.salocin.android.device.AndroidDevice
import de.salocin.android.device.AndroidDevicePath
import de.salocin.packagemanager.device.DevicePath
import de.salocin.packagemanager.io.OutputParser
import de.salocin.packagemanager.io.RegexOutputParser
import java.nio.file.Path

object Adb {

    private val devicesRegex = Regex("^([A-Z0-9]+).+model:([^\\s]*).*\$")
    private val packagesRegex = Regex("^package:(.+)=(.+)$")
    private val stripPackagePrefixRegex = Regex("^package:(.+)$")

    suspend fun devices(): List<AndroidDevice> {
        val parser = RegexOutputParser(devicesRegex).takeAllGroups().mapEachMatchTo { AndroidDevice(it[1], it[0]) }
        val process = AdbProcess.build(null, listOf("devices", "-l"), parser)
        return process.execute()
    }

    suspend fun pull(device: AndroidDevice, devicePath: DevicePath<AndroidDevice>, target: Path) {
        val process = AdbProcess.build(device, listOf("pull", "-a", devicePath.path, target.toString()))
        process.execute()
    }

    suspend fun install(device: AndroidDevice, files: Array<Path>) {
        val paths = files.map { it.toString() }.toTypedArray()
        val process = AdbProcess.build(device, listOf("install-multiple", *paths))
        process.execute()
    }

    suspend fun packages(device: AndroidDevice): List<AndroidApp> {
        val parser = RegexOutputParser(packagesRegex).takeAllGroups()
        val process = AdbProcess.build(device, listOf("shell", "pm", "list", "packages", "-f"), parser)
        return process.execute().map { AndroidApp(device, it[1], retrieveAppType(it[0])) }
    }

    private fun retrieveAppType(basePath: String): AndroidAppType {
        val isData = basePath.startsWith("/data")
        val isSystem = basePath.startsWith("/system")
        val isVendor = basePath.startsWith("/system_ext") || basePath.startsWith("/vendor")
                || basePath.startsWith("/product")

        return when {
            isData -> AndroidAppType.DATA
            isSystem -> AndroidAppType.SYSTEM
            isVendor -> AndroidAppType.VENDOR
            else -> AndroidAppType.UNKNOWN
        }
    }

    suspend fun packagePaths(device: AndroidDevice, name: String): List<AndroidDevicePath> {
        val parser = RegexOutputParser(stripPackagePrefixRegex).takeGroup(1).mapEachMatchTo {
            AndroidDevicePath(device, it)
        }
        val process = AdbProcess.build(device, listOf("shell", "pm", "path", name), parser)
        return process.execute()
    }

    suspend fun <T> shell(device: AndroidDevice, parser: OutputParser<T>, vararg command: String): List<T> {
        val process = AdbProcess.build(device, listOf("shell", *command), parser)
        return process.execute()
    }
}
