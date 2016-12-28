package net.yested.ext.bootstrap3

import net.yested.core.html.*
import net.yested.core.properties.*
import net.yested.core.utils.removeChildByName
import net.yested.core.utils.with
import org.w3c.dom.*
import java.util.*

interface ColumnDefinition {
    val css: String
}

sealed class Col {
    class Width {
        class Xs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xs-$width"
        }

        class Sm(val width: Int) : ColumnDefinition {
            override val css: String =  "col-sm-$width"
        }

        class Md(val width: Int) : ColumnDefinition {
            override val css: String =  "col-md-$width"
        }

        class Lg(val width: Int) : ColumnDefinition {
            override val css: String =  "col-lg-$width"
        }
    }
    class Offset {
        class Xs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xs-offset-$width"
        }

        class Sm(width: Int) : ColumnDefinition {
            override val css: String =  "col-sm-offset-$width"
        }

        class Md(width: Int) : ColumnDefinition {
            override val css: String =  "col-md-offset-$width"
        }

        class Lg(width: Int) : ColumnDefinition {
            override val css: String =  "col-lg-offset-$width"
        }
    }
    class Push {
        class Xs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xs-push-$width"
        }

        class Sm(width: Int) : ColumnDefinition {
            override val css: String =  "col-sm-push-$width"
        }

        class Md(width: Int) : ColumnDefinition {
            override val css: String =  "col-md-push-$width"
        }

        class Lg(width: Int) : ColumnDefinition {
            override val css: String =  "col-lg-push-$width"
        }
    }
    class Pull {
        class Xs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xs-pull-$width"
        }

        class Sm(width: Int) : ColumnDefinition {
            override val css: String =  "col-sm-pull-$width"
        }

        class Md(width: Int) : ColumnDefinition {
            override val css: String =  "col-md-pull-$width"
        }

        class Lg(width: Int) : ColumnDefinition {
            override val css: String =  "col-lg-pull-$width"
        }
    }
}

infix fun ColumnDefinition.and(other: ColumnDefinition) = object: ColumnDefinition {
    override val css: String = this@and.css + other.css
}
        
fun HTMLElement.row(init:HTMLDivElement.()->Unit) {
    div { className = "row"
        init()
    }
}

fun HTMLElement.col(width: ColumnDefinition, init: HTMLDivElement.()->Unit): HTMLDivElement {
    return div { className = width.css
        init()
    }
}

data class Column<in T>(
        val label: HTMLElement.() -> Unit,
        val sortFunction: ((T, T) -> Int)? = null,
        val align: Align = Align.LEFT,
        val sortAscending: Boolean? = null,
        val render: HTMLElement.(T) -> Unit) {
    init {
        if ((sortFunction == null) != (sortAscending == null)) {
            if (sortFunction == null) {
                throw IllegalArgumentException("sortFunction must be specified when sortAscending is specified")
            } else {
                throw IllegalArgumentException("sortAscending must be specified when sortFunction is specified")
            }
        }
    }
}

/** A Grid of data with a column per field, and the allowing the user to sort, if configured. */
fun <T> HTMLElement.grid(responsive: Boolean = false, columns: Array<Column<T>>, data: ReadOnlyProperty<Iterable<T>?>) {
    if (responsive) {
        div { className = "table-responsive"
            gridTable(columns, data)
        }
    } else {
        gridTable(columns, data)
    }
}

private fun <T> HTMLElement.gridTable(columns: Array<Column<T>>, data: ReadOnlyProperty<Iterable<T>?>) {
    data class ColumnSort<T>(val column: Column<T>, val ascending: Boolean)

    val firstColumn = columns.filter { it.sortAscending != null }.firstOrNull()
    val sortColumn: Property<ColumnSort<T>>? = firstColumn?.let { ColumnSort(firstColumn, firstColumn.sortAscending!!).toProperty() }

    fun sortData(toSort:Iterable<T>?, columnSort: ColumnSort<T>?):Iterable<T>? {
        val sortFunction = columnSort?.column?.sortFunction
        if (sortFunction == null || toSort == null) {
            return toSort
        }
        val ascending = columnSort?.ascending ?: true
        //return toSort.sortedWith(comparator = Comparator { t, t ->  })
        return toSort.sortedWith(comparator = Comparator { obj1: T, obj2: T ->  (sortFunction(obj1, obj2)) * (if (ascending) 1 else -1)})
    }

    val sortedData: ReadOnlyProperty<Iterable<T>?>
    if (sortColumn != null) {
        sortedData = data.zip<Iterable<T>?, ColumnSort<T>>(sortColumn).map { sortData(it.first, it.second) }
    } else {
        sortedData = data
    }

    fun sortByColumn(column: Column<T>) {
        if (column == sortColumn?.get()?.column) {
            val columnSort = sortColumn?.get()!!
            sortColumn!!.set(columnSort.copy(ascending = !columnSort.ascending))
        } else {
            sortColumn!!.set(ColumnSort(column, ascending = true))
        }
    }

    val tableElement = table() { className = "table table-striped table-hover table-condensed"
        thead {
            tr {
                columns.forEach { column ->
                    th { className = "text-${column.align.code}"
                        if (column.sortAscending == null) {
                            (column.label)()
                        } else {
                            a {
                                "style".."cursor: pointer;"
                                onclick = { sortByColumn(column) }
                                (column.label)()
                            }
                            span {
                                val icon = sortColumn!!.map { sortColumn ->
                                    if (sortColumn.column != column) null
                                    else if (sortColumn.ascending) "arrow-up" else "arrow-down"
                                }
                                glyphicon(icon)
                            }
                        }
                    }
                }
            }
        }
    }

    sortedData.onNext { values ->
        tableElement.removeChildByName("tbody")
        values?.let {
            tableElement.with {
                tbody {
                    values.forEach { item ->
                        tr {
                            columns.forEach { column ->
                                td {
                                    "class" .. "text-${column.align.code}";
                                    (column.render)(item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
