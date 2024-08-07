package de.salocin.ui.fontawesome

import de.salocin.ui.PackageManagerApplication
import javafx.scene.text.Font

private const val FONT_SIZE = 16.0

val FontAwesomeFont: Font =
    Font.loadFont(PackageManagerApplication::class.java.getResourceAsStream("/fa-solid.ttf"), FONT_SIZE)
