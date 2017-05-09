package net.yested.core.utils

import net.yested.core.properties.Property
import java.io.Serializable
import java.util.*
import kotlin.comparisons.compareBy
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

/** Compare two Property values.  This is especially useful when using a grid of Iterable<Property<T>>. */
fun <T> compareByProperty(get: (T) -> Comparable<*>?): Comparator<Property<T>> {
    return compareBy { get(it.get()) }
}

/**
 * Represents a tuple of 4 values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Tuple4 exhibits value semantics, i.e. two tuples are equal if all four components are equal.
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @property first First value.
 * @property second Second value.
 * @property third Third value.
 * @property fourth Fourth value.
 */
public data class Tuple4<out A, out B, out C, out D>(
        public val first: A,
        public val second: B,
        public val third: C,
        public val fourth: D
                                             ) : Serializable {

    /**
     * Returns string representation of the [Tuple4] including its [first], [second], [third] and [fourth] values.
     */
    public override fun toString(): String = "($first, $second, $third, $fourth)"
}

/**
 * Converts this tuple into a list.
 */
public fun <T> Tuple4<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)

/**
 * Represents a tuple of 5 values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Tuple5 exhibits value semantics, i.e. two tuples are equal if all five components are equal.
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @param E type of the fifth value.
 * @property first First value.
 * @property second Second value.
 * @property third Third value.
 * @property fourth Fourth value.
 * @property fifth Fifth value.
 */
public data class Tuple5<out A, out B, out C, out D, out E>(
        public val first: A,
        public val second: B,
        public val third: C,
        public val fourth: D,
        public val fifth: E
                                             ) : Serializable {

    /**
     * Returns string representation of the [Tuple5] including its [first], [second], [third], [fourth] and [fifth] values.
     */
    public override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"
}

/**
 * Converts this tuple into a list.
 */
public fun <T> Tuple5<T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth, fifth)

/**
 * Represents a tuple of 6 values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Tuple6 exhibits value semantics, i.e. two tuples are equal if all six components are equal.
 *
 * @param A type of the first value.
 * @param B type of the second value.
 * @param C type of the third value.
 * @param D type of the fourth value.
 * @param E type of the fifth value.
 * @param F type of the sixth value.
 * @property first First value.
 * @property second Second value.
 * @property third Third value.
 * @property fourth Fourth value.
 * @property fifth Fifth value.
 * @property sixth Sixth value.
 */
public data class Tuple6<out A, out B, out C, out D, out E, out F>(
        public val first: A,
        public val second: B,
        public val third: C,
        public val fourth: D,
        public val fifth: E,
        public val sixth: F
                                             ) : Serializable {

    /**
     * Returns string representation of the [Tuple5] including its [first], [second], [third], [fourth], [fifth], and [sixth] values.
     */
    public override fun toString(): String = "($first, $second, $third, $fourth, $fifth, $sixth)"
}

/**
 * Converts this tuple into a list.
 */
public fun <T> Tuple6<T, T, T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth, fifth, sixth)
