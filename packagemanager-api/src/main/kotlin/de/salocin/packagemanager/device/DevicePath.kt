package de.salocin.packagemanager.device

interface DevicePath<out D : Device> {

    val device: D

    val path: String

    suspend fun list(): List<DevicePath<D>>
}
