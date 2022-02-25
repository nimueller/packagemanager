package de.salocin.packagemanager.fake

import de.salocin.packagemanager.device.DevicePath

data class FakeDevicePath(
    override val device: FakeAndroidDevice,
    override val path: String
) : DevicePath<FakeAndroidDevice> {

    override suspend fun list(): List<DevicePath<FakeAndroidDevice>> {
        return emptyList()
    }

    override fun toString(): String {
        return path
    }
}
