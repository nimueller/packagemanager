package de.salocin.ui

import de.salocin.android.device.AndroidDeviceHolder
import de.salocin.packagemanager.device.Device
import de.salocin.packagemanager.fake.FakeAndroidDevice
import de.salocin.ui.dialog.CancelableProgressDialog
import de.salocin.ui.dialog.ProgressDialog
import de.salocin.ui.fontawesome.FA_SYNC
import de.salocin.ui.fontawesome.FA_UPLOAD
import de.salocin.ui.fontawesome.fontAwesomeButton
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.control.ComboBox
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import javafx.stage.Window
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Path

class ToolbarView(app: PackageManagerApplication, private val owner: Window) : ApplicationView(app) {

    private val devicesComboBox = ComboBox<Device>().apply {
        selectFirstOnNoSelection()
    }

    val selectedDevice: ObservableValue<Device> = devicesComboBox.selectionModel.selectedItemProperty()

    private val refreshButton = fontAwesomeButton("Refresh", FA_SYNC) {
        refreshDevicesJob()
    }

    private val installButton = fontAwesomeButton("Install", FA_UPLOAD) {
        val dialog = CancelableProgressDialog(owner)
        dialog.cancelableJob = app.launch {
            onInstall(dialog)
        }
    }

    override val root = HBox().apply {
        spacing = 5.0
        padding = Insets(10.0)

        children.add(devicesComboBox)
        children.add(refreshButton)
        children.add(installButton)
    }

    init {
        refreshDevicesJob()
    }

    private fun ComboBox<*>.selectFirstOnNoSelection() {
        if (selectionModel.selectedItem == null) {
            selectionModel.selectFirst()
        }
    }

    private fun refreshDevicesJob() {
        app.launch {
            devicesComboBox.items = null
            devicesComboBox.placeholder = ProgressIndicator()
            AndroidDeviceHolder.refreshDevices()
            devicesComboBox.items = if (AndroidDeviceHolder.devices.isEmpty()) {
                FXCollections.singletonObservableList(FakeAndroidDevice.noDevicesConnected)
            } else {
                AndroidDeviceHolder.devices.observableList()
            }
            devicesComboBox.selectFirstOnNoSelection()
        }
    }

    private suspend fun onInstall(dialog: ProgressDialog) {
        val file: File? = FileChooser().apply {
            extensionFilters.add(FileChooser.ExtensionFilter("Android Package", ".apk"))
            extensionFilters.add(FileChooser.ExtensionFilter("Split Android Packages", ".zip"))
        }.showOpenDialog(owner)

        if (file != null) {
            dialog.show()
            installFrom(dialog, file.toPath())
        }
    }

    private suspend fun installFrom(dialog: ProgressDialog, target: Path) {
        dialog.notifyProgressChange(0)
        dialog.notifyMaxProgressChange(1)

        TODO()
        /*
        for (source in paths) {
            observer?.notifyMessageChange("Downloading ${source.name}")
            val tempTarget = target.path.resolve(source.name)
            Adb.pull(source, tempTarget).execute()
            observer?.notifyProgressChange(++progress)
        }
         */
    }
}
