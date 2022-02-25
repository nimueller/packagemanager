package de.salocin.ui

import de.salocin.ui.details.PackageDetailsView
import de.salocin.ui.files.DeviceFileSystemTree
import javafx.application.HostServices
import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.stage.Window

class PackageView(
    app: PackageManagerApplication,
    owner: Window,
    hostServices: HostServices
) : ApplicationView(app) {

    private val toolbar = ToolbarView(app, owner)
    private val fileSystem = DeviceFileSystemTree(app, toolbar.selectedDevice)
    private val list = PackageListView(app, toolbar.selectedDevice)
    private val details = PackageDetailsView(app, owner, hostServices, list.selectedPackage)

    override val root = BorderPane().apply {
        top = toolbar.root

        val packageView = SplitPane(list.root, details.root).apply {
            orientation = Orientation.HORIZONTAL
            details.root.prefHeightProperty().bind(heightProperty())
        }

        val packageTab = Tab("Package View", packageView)
        val fileSystemTab = Tab("File System", fileSystem.root)

        center = TabPane(packageTab, fileSystemTab).apply {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        }
    }
}
