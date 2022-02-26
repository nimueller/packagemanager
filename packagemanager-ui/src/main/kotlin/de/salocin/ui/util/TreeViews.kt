package de.salocin.ui.util

import de.salocin.ui.observable
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.util.Callback
import kotlin.reflect.KProperty1

fun <S, T> TreeTableView<S>.column(
    title: String,
    extractor: (S) -> T,
    cellFactory: () -> TreeTableCell<S, T> = { TreeTableCell() }
) {
    val column = TreeTableColumn<S, T>(title)
    column.cellFactory = Callback { cellFactory() }
    column.cellValueFactory = Callback { features ->
        extractor(features.value.value).observable()
    }
    columns.add(column)
}

fun <S, T> TreeTableView<S>.column(
    title: String,
    prop: KProperty1<S, T>,
    cellFactory: () -> TreeTableCell<S, T> = { TreeTableCell() }
) {
    column(title, { value -> prop.get(value) }, cellFactory)
}
