package de.salocin.ui.files

import de.salocin.android.io.filename
import de.salocin.packagemanager.device.Device
import de.salocin.packagemanager.device.DevicePath
import de.salocin.packagemanager.device.FileType
import de.salocin.ui.fontawesome.FA_FILE_ALT
import de.salocin.ui.fontawesome.FA_FOLDER
import de.salocin.ui.fontawesome.fontAwesomeIcon
import javafx.scene.control.TreeTableCell

class DeviceFileSystemTreeTableCell : TreeTableCell<DevicePath<Device>, String?>() {

    private val regularIcon = fontAwesomeIcon(FA_FILE_ALT)
    private val folderIcon = fontAwesomeIcon(FA_FOLDER)

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        val value = treeTableRow?.treeItem?.value

        if (empty || item == null || value == null) {
            text = null
            graphic = null
        } else {
            text = item.filename
            graphic = when (value.type) {
                FileType.Regular -> regularIcon
                FileType.Directory -> folderIcon
                else -> null
            }
        }
    }
}
