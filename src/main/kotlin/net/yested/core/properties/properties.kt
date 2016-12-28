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

/**
 * Map a Property to another Property and vice-versa.
 * This is useful when either property can be modified and the other property should reflect the change,
 * but it should not circle back to again update the property that was modified.
 */
fun <IN, OUT> Property<IN>.mapBidirectionally(transform: (IN)->OUT, reverse: (OUT)->IN): Property<OUT> {
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
