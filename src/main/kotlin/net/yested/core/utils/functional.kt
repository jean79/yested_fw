package net.yested.core.utils

import net.yested.core.properties.Property
import kotlin.comparisons.compareValues

infix fun <T> T.with(doWith: T.()->Unit): T {
    this.doWith()
    return this
}

fun <T, V : Comparable<V>> compareByValue(get: (T) -> V?): (T, T) -> Int {
    return { l, r -> compareValues(get(l), get(r)) }
}

/** Compare two Property values.  This is especially useful when using a grid of Iterable<Property<T>>. */
fun <T, V : Comparable<V>> compareByPropertyValue(get: (T) -> V?): (Property<T>, Property<T>) -> Int {
    return compareByValue<Property<T>,V> { get(it.get()) }
}
