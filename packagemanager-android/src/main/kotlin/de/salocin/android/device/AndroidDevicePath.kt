package de.salocin.android.device

import de.salocin.android.adb.Adb
import de.salocin.packagemanager.device.DevicePath
import de.salocin.packagemanager.io.PlainOutputParser

data class AndroidDevicePath(
    override val device: AndroidDevice,
    override val path: String
) : DevicePath<AndroidDevice> {

    override suspend fun list(): List<AndroidDevicePath> {
        return Adb.shell(device, PlainOutputParser, "ls", path).mapNotNull { fileName ->
            if (fileName == path) {
                null
            } else {
                AndroidDevicePath(device, "$path/$fileName")
            }
        }
    }
}
