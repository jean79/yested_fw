package net.yested.core.properties

import net.yested.core.utils.SortSpecification
import net.yested.core.utils.Tuple4
import net.yested.core.utils.Tuple5
import net.yested.core.utils.Tuple6
import kotlin.browser.window

interface Disposable {
    fun dispose()
}

private interface PropertyChangeListener<in T> {
    fun onNext(value: T)
}

/** A Property that can be subscribed to.  The T value should be immutable or else its changes won't be detected. */
interface ReadOnlyProperty<out T> {
    fun get():T
    fun onNext(handler: (T)->Unit):Disposable
}

private val nullProperty: ReadOnlyProperty<Any?> = object : ReadOnlyProperty<Any?> {
    val emptyDisposable: Disposable = object : Disposable {
        override fun dispose() {}
    }

    override fun get(): Any? = null

    override fun onNext(handler: (Any?) -> Unit): Disposable {
        handler(null)
        return emptyDisposable
    }
}
@Suppress("UNCHECKED_CAST")
fun <T> nullProperty(): ReadOnlyProperty<T?> = nullProperty as ReadOnlyProperty<T>

/** A mutable Property that can be subscribed to.  The T value should be immutable or else its changes won't be detected. */
class Property<T>(initialValue: T): ReadOnlyProperty<T> {

    private var value : T = initialValue
    private var valueHashCode = value?.hashCode()
    private val listeners = mutableSetOf<PropertyChangeListener<T>>()

    fun set(newValue: T) {
        // check the hashCode in case the value is mutable, the same instance is provided, but its contents have changed.
        val newValueHashCode = newValue?.hashCode()
        if (newValue != value || newValueHashCode != valueHashCode) {
            value = newValue
            valueHashCode = newValueHashCode
            listeners.forEach {
                if ((value == newValue) && (valueHashCode == newValueHashCode)) {
                    it.onNext(value)
                } //else one of the listeners or another thread may have changed it again, which will have its own notification
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!(other is Property<*>)) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }

    override fun get() = value

    override fun onNext(handler: (T)->Unit):Disposable {
        val listener = object : PropertyChangeListener<T> {
            override fun onNext(value: T) {
                handler(value)
            }
        }
        listeners.add(listener)
        handler(value)
        return object : Disposable {
            override fun dispose() {
                listeners.remove(listener)
            }
        }
    }

    /** Useful for ensuring that listeners are disposed correctly. */
    val listenerCount: Int get() = listeners.size
}

fun <IN, OUT> ReadOnlyProperty<IN>.map(transform: (IN)->OUT): ReadOnlyProperty<OUT> {
    return mapAsDefault(transform)
}

/**
 * Map a property to a modifiable property.
 * The resulting property can be modified directly which will have no effect on the original property.
 * If the original property changes, it will get a new value using transform, losing any direct modifications made.
 */
fun <IN, OUT> ReadOnlyProperty<IN>.mapAsDefault(transform: (IN)->OUT): Property<OUT> {
    val property = Property(transform(this.get()))
    this.onNext {
        property.set(transform(it))
    }
    return property
}

fun <IN, OUT> ReadOnlyProperty<IN>.flatMap(transform: (IN)->ReadOnlyProperty<OUT>): ReadOnlyProperty<OUT> {
    val initialProperty = transform(this.get())
    val result = Property(initialProperty.get())
    var disposable = initialProperty.onChange { _, value -> result.set(value) }
    this.onChange { _, value ->
        disposable.dispose()
        disposable = transform(value).onNext { result.set(it) }
    }
    return result
}

fun <IN, OUT> ReadOnlyProperty<IN>.flatMapOrNull(transform: (IN)->ReadOnlyProperty<OUT?>?): ReadOnlyProperty<OUT?> {
    return flatMap<IN,OUT?> { transform(it) ?: nullProperty() }
}

fun <IN, OUT> ReadOnlyProperty<IN?>.flatMapIfNotNull(transform: (IN)->ReadOnlyProperty<OUT?>?): ReadOnlyProperty<OUT?> {
    return flatMapOrNull<IN?,OUT?> { it?.let(transform) }
}

fun <IN, OUT> ReadOnlyProperty<IN?>.mapIfNotNull(default: OUT? = null, transform: (IN)->OUT?): ReadOnlyProperty<OUT?> {
    return map { it?.let { transform(it) } ?: default }
}

/**
 * Returns a Property that is updated asynchronously from this Property.
 * Only the latest value is guaranteed to be propagated.
 * This is useful for deferring work that doesn't need to happen right away,
 * and to avoid redoing work that could be done once by waiting.
 */
fun <T> ReadOnlyProperty<T>.async(): ReadOnlyProperty<T> {
    val result = Property(get())
    onNext {
        window.requestAnimationFrame {
            result.set(get())
        }
    }
    return result
}

/**
 * Executes an operation each time the property value changes.  The operation is not called immediately.
 * @param operation an operation that takes the old and the new values, in that order.
 */
fun <T> ReadOnlyProperty<T>.onChange(operation: (T, T)->Unit): Disposable {
    var firstTime = true
    var oldValue = get()
    return onNext { newValue ->
        if (firstTime) {
            firstTime = false
        } else {
            val oldValueToUse = oldValue
            oldValue = newValue
            operation(oldValueToUse, newValue)
        }
    }
}

/**
 * Collects values for this property into a collected result.
 * The resulting Property's onNext is called each time another value is collected.
 * The collector function has access to the collected result so far and the next value.
 * The collected result is null the first time the collector function is run.
 * This can be useful when reducing or accumulating data or conditionally reusing what was previously mapped.
 */
fun <OUT, IN> ReadOnlyProperty<IN>.collect(collector: (OUT?, IN)->OUT): ReadOnlyProperty<OUT> {
    return collectAsDefault(collector)
}

/** Same as [collect] but return a modifiable Property like [mapAsDefault]. */
fun <OUT, IN> ReadOnlyProperty<IN>.collectAsDefault(collector: (OUT?, IN)->OUT): Property<OUT> {
    val collected = Property(collector(null, this.get()))
    var firstTime = true
    this.onNext { if (firstTime) { firstTime = false } else collected.set(collector(collected.get(), it)) }
    return collected
}

/** Maps two properties together to calculate a single result. */
fun <T,T2,OUT> ReadOnlyProperty<T>.mapWith(property2: ReadOnlyProperty<T2>, transform: (T,T2)->OUT): ReadOnlyProperty<OUT> {
    var value1 = this.get()
    var value2 = property2.get()
    val result = Property(transform(value1, value2))
    this.onNext {
        value1 = it
        result.set(transform(value1, value2))
    }
    property2.onNext {
        value2 = it
        result.set(transform(value1, value2))
    }
    return result
}

/** Maps three properties together to calculate a single result. */
fun <T,T2,T3,OUT> ReadOnlyProperty<T>.mapWith(property2: ReadOnlyProperty<T2>, property3: ReadOnlyProperty<T3>,
                                              transform: (T,T2,T3)->OUT): ReadOnlyProperty<OUT> {
    var value1 = this.get()
    var value2 = property2.get()
    var value3 = property3.get()
    val result = Property(transform(value1, value2, value3))
    this.onNext {
        value1 = it
        result.set(transform(value1, value2, value3))
    }
    property2.onNext {
        value2 = it
        result.set(transform(value1, value2, value3))
    }
    property3.onNext {
        value3 = it
        result.set(transform(value1, value2, value3))
    }
    return result
}

/**
 * Map a Property to another Property and vice-versa.
 * This is useful when either property can be modified and the other property should reflect the change,
 * but it should not circle back to again update the property that was modified.
 */
fun <IN, OUT> Property<IN>.bind(transform: (IN)->OUT, reverse: (OUT)->IN): Property<OUT> {
    var updating = false
    val transformedProperty = Property(transform(this.get()))
    this.onNext {
        if (!updating) {
            transformedProperty.set(transform(it))
        }
    }
    transformedProperty.onNext {
        updating = true
        try { this.set(reverse(it)) }
        finally { updating = false }
    }
    return transformedProperty
}

/**
 * Map a Property to two Properties and vice-versa.
 * This is useful when either property can be modified and the other property should reflect the change,
 * but it should not circle back to again update the property that was modified.
 */
fun <IN, OUT1, OUT2> Property<IN>.bindParts(transform1: (IN)->OUT1, transform2: (IN)->OUT2, reverse: (OUT1, OUT2)->IN): Pair<Property<OUT1>, Property<OUT2>> {
    var updating = false
    val transformedProperty1 = Property(transform1(this.get()))
    val transformedProperty2 = Property(transform2(this.get()))
    this.onNext {
        if (!updating) {
            transformedProperty1.set(transform1(it))
            transformedProperty2.set(transform2(it))
        }
    }
    transformedProperty1.onNext {
        updating = true
        try { this.set(reverse(it, transformedProperty2.get())) }
        finally { updating = false }
    }
    transformedProperty2.onNext {
        updating = true
        try { this.set(reverse(transformedProperty1.get(), it)) }
        finally { updating = false }
    }
    return Pair(transformedProperty1, transformedProperty2)
}

fun ReadOnlyProperty<Boolean>.not() = this.map { !it }

/** Zips two properties together into a Pair. */
fun <T,T2> ReadOnlyProperty<T>.zip(property2: ReadOnlyProperty<T2>): ReadOnlyProperty<Pair<T,T2>> {
    var value1 = this.get()
    var value2 = property2.get()
    val combined = Property(Pair(value1, value2))
    this.onNext {
        value1 = it
        combined.set(Pair(value1, value2))
    }
    property2.onNext {
        value2 = it
        combined.set(Pair(value1, value2))
    }
    return combined
}

/** Zips three properties together into a Triple. */
fun <T,T2,T3> ReadOnlyProperty<T>.zip(property2: ReadOnlyProperty<T2>, property3: ReadOnlyProperty<T3>): ReadOnlyProperty<Triple<T,T2,T3>> {
    var value1 = this.get()
    var value2 = property2.get()
    var value3 = property3.get()
    val combined = Property(Triple(value1, value2, value3))
    this.onNext {
        value1 = it
        combined.set(Triple(value1, value2, value3))
    }
    property2.onNext {
        value2 = it
        combined.set(Triple(value1, value2, value3))
    }
    property3.onNext {
        value3 = it
        combined.set(Triple(value1, value2, value3))
    }
    return combined
}

/** Zips four properties together into a Tuple4. */
fun <T,T2,T3,T4> ReadOnlyProperty<T>.zip(property2: ReadOnlyProperty<T2>, property3: ReadOnlyProperty<T3>, property4: ReadOnlyProperty<T4>): ReadOnlyProperty<Tuple4<T, T2, T3, T4>> {
    var value1 = this.get()
    var value2 = property2.get()
    var value3 = property3.get()
    var value4 = property4.get()
    val combined = Property(Tuple4(value1, value2, value3, value4))
    this.onNext {
        value1 = it
        combined.set(Tuple4(value1, value2, value3, value4))
    }
    property2.onNext {
        value2 = it
        combined.set(Tuple4(value1, value2, value3, value4))
    }
    property3.onNext {
        value3 = it
        combined.set(Tuple4(value1, value2, value3, value4))
    }
    property4.onNext {
        value4 = it
        combined.set(Tuple4(value1, value2, value3, value4))
    }
    return combined
}

/** Zips five properties together into a Tuple5. */
fun <T,T2,T3,T4,T5> ReadOnlyProperty<T>.zip(property2: ReadOnlyProperty<T2>, property3: ReadOnlyProperty<T3>, property4: ReadOnlyProperty<T4>, property5: ReadOnlyProperty<T5>): ReadOnlyProperty<Tuple5<T, T2, T3, T4, T5>> {
    var value1 = this.get()
    var value2 = property2.get()
    var value3 = property3.get()
    var value4 = property4.get()
    var value5 = property5.get()
    val combined = Property(Tuple5(value1, value2, value3, value4, value5))
    this.onNext {
        value1 = it
        combined.set(Tuple5(value1, value2, value3, value4, value5))
    }
    property2.onNext {
        value2 = it
        combined.set(Tuple5(value1, value2, value3, value4, value5))
    }
    property3.onNext {
        value3 = it
        combined.set(Tuple5(value1, value2, value3, value4, value5))
    }
    property4.onNext {
        value4 = it
        combined.set(Tuple5(value1, value2, value3, value4, value5))
    }
    property5.onNext {
        value5 = it
        combined.set(Tuple5(value1, value2, value3, value4, value5))
    }
    return combined
}

/** Zips six properties together into a Tuple6. */
fun <T,T2,T3,T4,T5,T6> ReadOnlyProperty<T>.zip(property2: ReadOnlyProperty<T2>, property3: ReadOnlyProperty<T3>, property4: ReadOnlyProperty<T4>, property5: ReadOnlyProperty<T5>, property6: ReadOnlyProperty<T6>): ReadOnlyProperty<Tuple6<T, T2, T3, T4, T5, T6>> {
    var value1 = this.get()
    var value2 = property2.get()
    var value3 = property3.get()
    var value4 = property4.get()
    var value5 = property5.get()
    var value6 = property6.get()
    val combined = Property(Tuple6(value1, value2, value3, value4, value5, value6))
    this.onNext {
        value1 = it
        combined.set(Tuple6(value1, value2, value3, value4, value5, value6))
    }
    property2.onNext {
        value2 = it
        combined.set(Tuple6(value1, value2, value3, value4, value5, value6))
    }
    property3.onNext {
        value3 = it
        combined.set(Tuple6(value1, value2, value3, value4, value5, value6))
    }
    property4.onNext {
        value4 = it
        combined.set(Tuple6(value1, value2, value3, value4, value5, value6))
    }
    property5.onNext {
        value5 = it
        combined.set(Tuple6(value1, value2, value3, value4, value5, value6))
    }
    property6.onNext {
        value6 = it
        combined.set(Tuple6(value1, value2, value3, value4, value5, value6))
    }
    return combined
}

/**
 * Combines two properties into another one that pairs them together.
 * @deprecated use [zip] which has the exact same behavior.
 */
fun <V1, V2> ReadOnlyProperty<V1>.combineLatest(other: ReadOnlyProperty<V2>): ReadOnlyProperty<Pair<V1,V2>> {
    return zip(other)
}

infix fun <T> ReadOnlyProperty<T>.debug(render: (T)->String):ReadOnlyProperty<T> {
    this.onNext { println(render(it)) }
    return this
}

fun <T> T.toProperty() = Property(this)

fun <T> Property<T>.modify(f: (T) -> T) { set(f(get())) }

fun <T> Property<List<T>>.modifyList(operation: (ArrayList<T>) -> Unit) {
    modify { list ->
        val newList = ArrayList(list)
        operation(newList)
        newList
    }
}

fun <T> Property<List<T>>.clear() { modifyList { it.clear() } }
fun <T> Property<List<T>>.removeAt(index: Int) { modifyList { it.removeAt(index) } }
fun <T> Property<List<T>>.add(item: T) { modifyList { it.add(item) } }
fun <T> Property<List<T>>.remove(item: T) { modifyList { it.remove(item) } }

fun <T> ReadOnlyProperty<Iterable<T>?>.sortedWith(sortSpecification: ReadOnlyProperty<SortSpecification<T>?>): ReadOnlyProperty<Iterable<T>?> {
    return sortedWith(sortSpecification.map { it?.fullComparator })
}

fun <T> ReadOnlyProperty<Iterable<T>?>.sortedWith(comparator: ReadOnlyProperty<Comparator<in T>?>): ReadOnlyProperty<Iterable<T>?> {
    return mapWith(comparator) { toSort, _comparator ->
        if (_comparator == null || toSort == null) {
            toSort
        } else {
            toSort.sortedWith(_comparator)
        }
    }
}
