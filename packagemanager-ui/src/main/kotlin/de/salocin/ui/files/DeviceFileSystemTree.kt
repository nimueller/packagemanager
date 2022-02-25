package de.salocin.ui.files

import de.salocin.android.device.FakeAndroidDevice
import de.salocin.android.device.FakeDevicePath
import de.salocin.android.io.filename
import de.salocin.packagemanager.device.Device
import de.salocin.packagemanager.device.DevicePath
import de.salocin.ui.ApplicationView
import de.salocin.ui.PackageManagerApplication
import de.salocin.ui.mapTo
import de.salocin.ui.observable
import javafx.beans.value.ObservableValue
import javafx.scene.control.*
import javafx.util.Callback
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

class DeviceFileSystemTree(
    app: PackageManagerApplication,
    selectedDevice: ObservableValue<Device>
) : ApplicationView(app) {

    private val placeholderItem = TreeItem<DevicePath<Device>>().apply {
        value = FakeDevicePath(FakeAndroidDevice("", ""), "")
    }

    override val root = TreeTableView<DevicePath<Device>>().apply {
        column("Name", DevicePath<Device>::path) { path -> path.filename }
        columnResizePolicy = TreeTableView.CONSTRAINED_RESIZE_POLICY
        placeholder = ProgressIndicator()
    }

    val selectedPath = root.selectionModel.selectedItemProperty().mapTo { it?.value }

    init {
        selectedDevice.addListener { _, _, device ->
            device?.let(this::refreshListJob)
        }

        selectedDevice.value?.let { device ->
            refreshListJob(device)
        }
    }

    private fun refreshListJob(device: Device) {
        app.launch {
            val rootItem: TreeItem<DevicePath<Device>>?
            root.root = null

            val roots = device.refreshRootPaths()

            if (roots.size == 1) {
                rootItem = roots.first().buildTreeItem(null)
                rootItem.isExpanded = true
            } else {
                rootItem = TreeItem<DevicePath<Device>>()

                for (rootPath in device.refreshRootPaths()) {
                    rootPath.buildTreeItem(rootItem)
                }
            }


            rootItem.value = FakeDevicePath(FakeAndroidDevice(device.serialNumber, device.model), device.model)
            root.root = rootItem
        }
    }

    private fun <T : Any?> TreeTableView<DevicePath<Device>>.column(
        title: String,
        prop: KProperty1<DevicePath<Device>, T>,
        converter: (T) -> T = { it }
    ) {
        val column = TreeTableColumn<DevicePath<Device>, T>(title)
        column.cellValueFactory = Callback {
            converter(prop.get(it.value.value)).observable()
        }
        columns.add(column)
    }

    private fun DevicePath<Device>.buildTreeItem(parent: TreeItem<DevicePath<Device>>?): TreeItem<DevicePath<Device>> {
        val item = TreeItem(this)

        item.expandedProperty().addListener { _, _, newValue ->
            if (newValue) {
                app.launch {
                    item.children.clear()
                    item.children.add(placeholderItem)
                    val children = list()
                    item.children.clear()
                    item.children.addAll(children.map { it.buildTreeItem(item) })
                }
            }
        }

        item.children.add(placeholderItem)
        parent?.children?.add(item)
        return item
    }
}
