package net.yested.core.properties

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

fun <IN, OUT> ReadOnlyProperty<IN>.map(transform: (IN)->OUT):ReadOnlyProperty<OUT> {
    val property = Property(transform(this.get()))
    this.onNext {
        property.set(transform(it))
    }
    return property
}

fun ReadOnlyProperty<Boolean>.not() = this.map { !it }

infix fun <T> ReadOnlyProperty<T>.debug(render: (T)->String):ReadOnlyProperty<T> {
    this.onNext { println(render(it)) }
    return this
}

fun <T> T.toProperty() = Property(this)