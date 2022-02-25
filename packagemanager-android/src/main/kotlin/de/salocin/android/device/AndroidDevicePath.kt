package de.salocin.android.device

import de.salocin.android.adb.Adb
import de.salocin.android.io.LS_OUTPUT_PARSER
import de.salocin.android.io.parseAsFileType
import de.salocin.packagemanager.device.DevicePath
import de.salocin.packagemanager.device.FileType

data class AndroidDevicePath(
    override val device: AndroidDevice,
    override val path: String,
    override val type: FileType
) : DevicePath<AndroidDevice> {

    override suspend fun list(): List<AndroidDevicePath> {
        return Adb.shell(device, LS_OUTPUT_PARSER, "ls", "-lA", path).mapNotNull { result ->
            val fileType = result.type.parseAsFileType()

            if (result.name == path || fileType == null) {
                null
            } else {
                AndroidDevicePath(device, "$path/${result.name}", fileType)
            }
        }
    }
}
