package net.yested.core.properties

import java.util.*

interface Disposable {
    fun dispose()
}

private interface PropertyChangeListener<in T> {
    fun onNext(value: T)
}

interface ReadOnlyProperty<out T> {
    fun get():T
    fun onNext(handler: (T)->Unit):Disposable
}

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
            listeners.forEach { it.onNext(value) }
        }
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
