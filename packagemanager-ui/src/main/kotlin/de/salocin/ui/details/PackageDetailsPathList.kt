package de.salocin.ui.details

import de.salocin.packagemanager.device.Device
import de.salocin.packagemanager.device.DevicePath
import de.salocin.ui.View
import de.salocin.ui.fontawesome.FA_COPY
import de.salocin.ui.fontawesome.fontAwesomeMenuItem
import de.salocin.ui.getValue
import de.salocin.ui.mapTo
import de.salocin.ui.setValue
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.util.Callback

class PackageDetailsPathList(label: String) : View {

    private val listLabel = Text(label)
    private val listView = ListView<DevicePath<Device>>().apply {
        cellFactory = Callback {
            val cell = ListCell<DevicePath<Device>>()
            val contextMenu = ContextMenu()
            contextMenu.items.add(fontAwesomeMenuItem("Copy Path", FA_COPY) {
                onAction = EventHandler {
                    val clipboard = Clipboard.getSystemClipboard()
                    val content = ClipboardContent()
                    content.putString(cell.item.path)
                    clipboard.setContent(content)
                }
            })

            cell.textProperty().bind(cell.itemProperty().mapTo { it?.path })
            cell.contextMenuProperty().bind(cell.emptyProperty().mapTo { if (it) null else contextMenu })

            cell
        }
    }

    var items: ObservableList<DevicePath<Device>> by listView.itemsProperty()

    override val root = VBox(listLabel, listView)
}
