package de.salocin.packagemanager.device

interface FileType {

    val isDirectory: Boolean

    val isRegular: Boolean

    val isLink: Boolean

    object Directory : FileType {

        override val isDirectory: Boolean = true

        override val isRegular: Boolean = false

        override val isLink: Boolean = false
    }

    object Regular : FileType {

        override val isDirectory: Boolean = false

        override val isRegular: Boolean = true

        override val isLink: Boolean = false
    }

    object Link : FileType {

        override val isDirectory: Boolean = false

        override val isRegular: Boolean = false

        override val isLink: Boolean = true
    }
}
