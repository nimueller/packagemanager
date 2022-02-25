package de.salocin.packagemanager.device

interface DevicePath<out D : Device> {

    val device: D

    val path: String

    val type: FileType

    suspend fun list(): List<DevicePath<D>>
}
