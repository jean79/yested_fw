package net.yested.core.utils

import net.yested.core.html.*
import net.yested.core.properties.Property
import net.yested.core.properties.mapAsDefault
import org.w3c.dom.*
import kotlin.comparisons.*

/**
 * Makes a [th] be sortable.
 * @param currentSort the Property that controls the sorting of a list. See [net.yested.core.properties.sortedWith].
 * @param comparator see [kotlin.comparisons.compareByValue]
 * @return a Boolean Property that is null if inactive, true if ascending, and false if descending.
 * It is useful for displaying an arrow or other indicator or for modifying the currentSort as a Boolean? for convenience.
 * Example:<pre>
 *   val currentSort = Property<SortSpecification<String>?>(null)
 *   val sortedData = data.sortedWith(currentSort)
 *   table {
 *       thead {
 *           th { sortControl(currentSort, compareBy<Item> { it.name }) { appendText("Name") } }
 *           th { sortControl(currentSort, compareBy<item> { it.quantity }) { appendText("Quantity") } }
 *       }
 *       tbody(sortedData) { item -> ... }
 *   }
 * </pre>
 *
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 2/3/17
 * Time: 4:51 PM
 */
fun <T> HTMLTableCellElement.sortControl(currentSort: Property<SortSpecification<T>?>,
                                         comparator: Comparator<T>,
                                         sortAscending: Boolean = true,
                                         sortNow: Boolean = false,
                                         init: HTMLElement.() -> Unit): Property<Boolean?> {
    return sortControl(currentSort, SortSpecification(comparator, sortAscending), sortNow, init)
}

fun <T> HTMLTableCellElement.sortControl(currentSort: Property<SortSpecification<T>?>,
                                         sortSpecification: SortSpecification<T>,
                                         sortNow: Boolean = false,
                                         init: HTMLElement.() -> Unit): Property<Boolean?> {
    val sortControlProperty = currentSort.mapAsDefault {
        if (it == null || it.sortableId != sortSpecification.sortableId) null else it.ascending
    }
    sortControlProperty.onNext {
        if (it != null) {
            currentSort.set(if (it) sortSpecification else sortSpecification.reverse)
        } else if (currentSort.get()?.sortableId == sortSpecification.sortableId) {
            currentSort.set(null)
        } // else leave it alone
    }
    if (sortNow) sortControlProperty.set(sortSpecification.ascending)
    a {
        setAttribute("style", "cursor: pointer;")
        onclick = { sortControlProperty.set(sortControlProperty.get()?.let { !it } ?: sortSpecification.ascending) }
        init()
    }
    return sortControlProperty
}

class SortSpecification<in T> private constructor (val comparator: Comparator<in T>, val ascending: Boolean, val sortableId: Int) {
    constructor(comparator: Comparator<T>, ascending: Boolean = true) : this(comparator, ascending, nextSortableId++)

    val fullComparator: Comparator<in T>? = if (ascending) comparator else comparator.reversed()
    val reverse: SortSpecification<T> by lazy { SortSpecification(comparator, !ascending, sortableId) }

    private companion object {
        private var nextSortableId: Int = 1
    }
}
