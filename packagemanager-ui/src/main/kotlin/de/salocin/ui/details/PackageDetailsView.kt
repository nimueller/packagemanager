package de.salocin.ui.details

import de.salocin.packagemanager.device.App
import de.salocin.packagemanager.fake.FakeAppType
import de.salocin.ui.ApplicationView
import de.salocin.ui.PackageManagerApplication
import de.salocin.ui.observableList
import javafx.application.HostServices
import javafx.beans.value.ObservableValue
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TitledPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Window
import kotlinx.coroutines.launch

class PackageDetailsView(
    app: PackageManagerApplication,
    owner: Window,
    hostServices: HostServices,
    selectedPackage: ObservableValue<App?>
) : ApplicationView(app) {

    private val buttons = PackageDetailsButtons(app, owner, hostServices, selectedPackage)
    private val nameTextField = PackageDetailsTextField("Name")
    private val pathsList = PackageDetailsPathList("Paths")

    private val content = VBox(buttons.root, nameTextField.root, pathsList.root).apply { isVisible = false }
    private val contentLoader = ProgressIndicator().apply { isVisible = false }
    private val contentNoDetails = Text("No details for selected package")
    private val contentPane = StackPane(content, contentLoader, contentNoDetails)

    override val root = TitledPane("Details", contentPane).apply {
        isCollapsible = false
    }

    init {
        selectedPackage.addListener { _, _, newValue ->
            content.isVisible = false
            contentLoader.isVisible = false

            if ((newValue == null) || (newValue.type == FakeAppType)) {
                contentNoDetails.isVisible = true
            } else {
                contentNoDetails.isVisible = false
                refreshDetailsJob(newValue)
            }
        }
    }

    private fun refreshDetailsJob(pack: App) {
        app.launch {
            contentLoader.isVisible = true

            pack.refreshPaths()

            nameTextField.text = pack.name
            pathsList.items = pack.paths.observableList()

            content.isVisible = true
            contentLoader.isVisible = false
        }
    }
}
