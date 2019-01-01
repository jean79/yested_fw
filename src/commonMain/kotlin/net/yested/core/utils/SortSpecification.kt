package net.yested.core.utils

class SortSpecification<in T> private constructor (val comparator: Comparator<in T>, val ascending: Boolean, val sortableId: Int) {
    constructor(comparator: Comparator<T>, ascending: Boolean = true) : this(comparator, ascending, nextSortableId++)

    val fullComparator: Comparator<in T>? = if (ascending) comparator else comparator.reversed()
    val reverse: SortSpecification<T> by lazy { SortSpecification(comparator, !ascending, sortableId) }

    private companion object {
        private var nextSortableId: Int = 1
    }
}
