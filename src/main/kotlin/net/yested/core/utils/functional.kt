package net.yested.core.utils

import kotlin.comparisons.compareValues

infix fun <T> T.with(doWith: T.()->Unit): T {
    this.doWith()
    return this
}

fun <T, V : Comparable<V>> compareByValue(get: (T) -> V?): (T, T) -> Int {
   return { l, r -> compareValues(get(l), get(r)) }
}
