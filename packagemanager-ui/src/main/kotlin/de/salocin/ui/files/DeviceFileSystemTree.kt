package de.salocin.ui.files

import de.salocin.packagemanager.device.Device
import de.salocin.packagemanager.device.DevicePath
import de.salocin.packagemanager.fake.FakeAndroidDevice
import de.salocin.packagemanager.fake.FakeDevicePath
import de.salocin.ui.ApplicationView
import de.salocin.ui.PackageManagerApplication
import de.salocin.ui.mapTo
import de.salocin.ui.util.column
import javafx.beans.value.ObservableValue
import javafx.scene.control.*
import kotlinx.coroutines.launch

class DeviceFileSystemTree(
    app: PackageManagerApplication,
    selectedDevice: ObservableValue<Device>
) : ApplicationView(app) {

    private val placeholderItem = TreeItem<DevicePath<Device>>().apply {
        value = FakeDevicePath(FakeAndroidDevice("", ""), "")
    }

    override val root = TreeTableView<DevicePath<Device>>().apply {
        column("Name", DevicePath<Device>::path, ::DeviceFileSystemTreeTableCell)
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

    private fun DevicePath<Device>.buildTreeItem(parent: TreeItem<DevicePath<Device>>?): TreeItem<DevicePath<Device>> {
        val item = TreeItem(this)

        if (type.isDirectory) {
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
        }

        parent?.children?.add(item)
        return item
    }
}
