package de.salocin.packagemanager.device

@JvmInline
value class DevicePath(val path: String) {

    override fun toString(): String {
        return path
    }
}
