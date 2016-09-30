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
    private val listeners = mutableSetOf<PropertyChangeListener<T>>()

    fun set(newValue: T) {
        if (newValue != value) {
            value = newValue
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

fun ReadOnlyProperty<Boolean>.not() = this.map { !it }

/** Combines two properties into another one that pairs them together. */
fun <T,T2> ReadOnlyProperty<T>.combineLatest(property2: ReadOnlyProperty<T2>): ReadOnlyProperty<Pair<T,T2>> {
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
