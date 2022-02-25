package de.salocin.ui

import de.salocin.android.device.AndroidAppType
import de.salocin.packagemanager.device.App
import de.salocin.packagemanager.device.Device
import de.salocin.packagemanager.fake.FakeAndroidApp
import javafx.beans.value.ObservableValue
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.util.Callback
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

class PackageListView(
    app: PackageManagerApplication,
    selectedDevice: ObservableValue<Device>
) : ApplicationView(app) {

    private val rootItem = TreeItem<App>().apply {
        isExpanded = true
    }

    private val dataPackagesItem =
        TreeItem<App>(FakeAndroidApp("Data")).apply {
            rootItem.children.add(this)
            isExpanded = true
        }

    private val systemPackagesItem =
        TreeItem<App>(FakeAndroidApp("System")).apply {
            rootItem.children.add(this)
        }

    private val vendorPackagesItem =
        TreeItem<App>(FakeAndroidApp("Vendor")).apply {
            rootItem.children.add(this)
        }

    private val unknownPackagesItem =
        TreeItem<App>(FakeAndroidApp("Unknown")).apply {
            rootItem.children.add(this)
        }

    override val root = TreeTableView<App>().apply {
        column("Name", App::name)
        columnResizePolicy = TreeTableView.CONSTRAINED_RESIZE_POLICY
        placeholder = ProgressIndicator()
    }

    val selectedPackage = root.selectionModel.selectedItemProperty().mapTo { it?.value }

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
            root.root = null

            device.refreshApps()

            dataPackagesItem.addPackages(device.apps.filter { it.type == AndroidAppType.DATA })
            systemPackagesItem.addPackages(device.apps.filter { it.type == AndroidAppType.SYSTEM })
            vendorPackagesItem.addPackages(device.apps.filter { it.type == AndroidAppType.VENDOR })
            unknownPackagesItem.addPackages(device.apps.filter { it.type == AndroidAppType.UNKNOWN })

            rootItem.value = FakeAndroidApp(device.toString())
            root.root = rootItem
        }
    }

    private fun <T : Any?> TreeTableView<App>.column(
        title: String,
        prop: KProperty1<App, T>
    ) {
        val column = TreeTableColumn<App, T>(title)
        column.cellValueFactory = Callback { prop.get(it.value.value).observable() }
        columns.add(column)
    }

    private fun TreeItem<App>.addPackages(packages: List<App>) {
        packages.sortedBy(App::name).forEach { pack ->
            children.add(TreeItem(pack))
        }
    }
}
