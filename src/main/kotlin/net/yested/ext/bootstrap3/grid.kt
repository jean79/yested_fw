package net.yested.ext.bootstrap3

import net.yested.core.html.*
import net.yested.core.properties.*
import net.yested.core.utils.SortSpecification
import net.yested.core.utils.sortControl
import org.w3c.dom.*
import java.util.*

interface ColumnDefinition {
    val css: String
}

sealed class Col {
    /** Widths are in 1/12's so a width of 12 means width:100% and 3 means width:25%. */
    class Width {
        /** Using this size requires including <a href="https://www.npmjs.com/package/bootstrap-xxs">bootstrap-xxs.css</a>. */
        class Tn(width: Int) : ColumnDefinition {
            override val css: String =  "col-tn-$width"
        }

        /** Using this size requires including <a href="https://www.npmjs.com/package/bootstrap-xxs">bootstrap-xxs.css</a>. */
        class Xxs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xxs-$width"
        }

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
    /** Offsets are in 1/12's so a 3 means push it right 25%. */
    class Offset {
        /** Using this size requires including <a href="https://www.npmjs.com/package/bootstrap-xxs">bootstrap-xxs.css</a>. */
        class Tn(width: Int) : ColumnDefinition {
            override val css: String =  "col-tn-offset-$width"
        }

        /** Using this size requires including <a href="https://www.npmjs.com/package/bootstrap-xxs">bootstrap-xxs.css</a>. */
        class Xss(width: Int) : ColumnDefinition {
            override val css: String =  "col-xxs-offset-$width"
        }

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
    /** Push widths are in 1/12's so a 3 means reorder it to the right by 25%. */
    class Push {
        /** Using this size requires including <a href="https://www.npmjs.com/package/bootstrap-xxs">bootstrap-xxs.css</a>. */
        class Tn(width: Int) : ColumnDefinition {
            override val css: String =  "col-tn-push-$width"
        }

        /** Using this size requires including <a href="https://www.npmjs.com/package/bootstrap-xxs">bootstrap-xxs.css</a>. */
        class Xxs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xxs-push-$width"
        }

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
    /** Pull widths are in 1/12's so a 3 means reorder it to the left by 25%. */
    class Pull {
        /** Using this size requires including <a href="https://www.npmjs.com/package/bootstrap-xxs">bootstrap-xxs.css</a>. */
        class Tn(width: Int) : ColumnDefinition {
            override val css: String =  "col-tn-pull-$width"
        }

        /** Using this size requires including <a href="https://www.npmjs.com/package/bootstrap-xxs">bootstrap-xxs.css</a>. */
        class Xxs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xxs-pull-$width"
        }

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
    override val css: String = this@and.css + " " + other.css
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

/** Note: Here for backward-compatibility. */
fun <T> Column(label: HTMLElement.() -> Unit,
               sortFunction: ((T, T) -> Int)? = null,
               align: Align = Align.LEFT,
               sortAscending: Boolean? = null,
               render: HTMLElement.(T) -> Unit): Column<T> {
    val comparator: Comparator<T>? = sortFunction?.let { Comparator { obj1: T, obj2: T -> (it(obj1, obj2)) * (if (sortAscending!!) 1 else -1) } }
    return Column(label, comparator, align, sortAscending, render)
}

/** Note: This would be <in T> except that Comparator isn't labeled as having <in T>. */
data class Column<T>(
        val label: HTMLElement.() -> Unit,
        val comparator: Comparator<T>? = null,
        val align: Align = Align.LEFT,
        val sortAscending: Boolean? = null,
        val render: HTMLElement.(T) -> Unit) {
    init {
        if ((comparator == null) != (sortAscending == null)) {
            if (comparator == null) {
                throw IllegalArgumentException("comparator must be specified when sortAscending is specified")
            } else {
                throw IllegalArgumentException("sortAscending must be specified when comparator is specified")
            }
        }
    }
}

/** A Grid of data with a column per field, and the allowing the user to sort, if configured. */
fun <T> HTMLElement.grid(responsive: Boolean = false, columns: Array<Column<T>>, data: ReadOnlyProperty<Iterable<T>?>,
                         sortColumn: Property<Column<T>?> = columns.filter { it.sortAscending != null }.firstOrNull().toProperty()) {
    if (responsive) {
        div { className = "table-responsive"
            gridTable(columns, data, sortColumn)
        }
    } else {
        gridTable(columns, data, sortColumn)
    }
}

private fun <T> HTMLElement.gridTable(columns: Array<Column<T>>, data: ReadOnlyProperty<Iterable<T>?>,
                                      sortColumn: Property<Column<T>?>) {
    data class ColumnSort<T>(val column: Column<T>, val ascending: Boolean) {
        val comparator: Comparator<T>? = column.comparator
    }

    val columnSort: Property<ColumnSort<T>?> = sortColumn.mapAsDefault { it?.let { ColumnSort(it, it.sortAscending!!) } }
    val sortSpecification: Property<SortSpecification<T>?> = columnSort.mapAsDefault {
        if (it != null && it.comparator != null) SortSpecification(it.comparator, it.ascending) else null
    }
    val sortedData = data.sortedWith(sortSpecification)

    table { className = "table table-striped table-hover table-condensed"
        thead {
            tr {
                columns.forEach { column ->
                    th {
                        className = "text-${column.align.code}"
                        if (column.comparator == null) {
                            (column.label)()
                        } else {
                            sortControlWithArrow<T>(sortSpecification, column.comparator, column.sortAscending!!) {
                                (column.label)()
                            }
                        }
                    }
                }
            }
        }
        tbody(sortedData) { item ->
            tr {
                columns.forEach { column ->
                    td {
                        className = "text-${column.align.code}"
                        (column.render)(item)
                    }
                }
            }
        }
    }
}

fun <T> HTMLTableHeaderCellElement.sortControlWithArrow(currentSort: Property<SortSpecification<T>?>,
                                                        comparator: Comparator<T>, sortAscending: Boolean = true,
                                                        sortNow: Boolean = false, init: HTMLElement.() -> Unit): Property<Boolean?> {
    val ascendingProperty = sortControl(currentSort, comparator, sortAscending, sortNow, init)
    span {
        val icon = ascendingProperty.map { ascending ->
            hidden = ascending == null
            if (ascending == null) null else if (ascending) "arrow-up" else "arrow-down"
        }
        glyphicon(icon)
    }
    return ascendingProperty
}
