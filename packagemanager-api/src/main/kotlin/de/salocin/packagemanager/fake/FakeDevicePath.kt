package de.salocin.packagemanager.fake

import de.salocin.packagemanager.device.DevicePath
import de.salocin.packagemanager.device.FileType

data class FakeDevicePath(
    override val device: FakeAndroidDevice,
    override val path: String
) : DevicePath<FakeAndroidDevice> {

    override val type: FileType = object : FileType {
        override val isDirectory: Boolean = false
        override val isRegular: Boolean = false
        override val isLink: Boolean = false
    }

    override suspend fun list(): List<DevicePath<FakeAndroidDevice>> {
        return emptyList()
    }

    override fun toString(): String {
        return path
    }
}
