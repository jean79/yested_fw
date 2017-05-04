package net.yested.ext.jquery

import net.yested.core.properties.Property
import net.yested.core.properties.bind
import org.w3c.dom.Location
import kotlin.browser.window

@native
private interface JQRouter {
    fun on(eventName:String, listener:() -> Unit):Unit = noImpl
    fun trigger(eventName:String):Unit = noImpl
}

@native("$(window)") private var routerJQuery: JQRouter = null!!

/** A Property for window.location.hash.  It is bound to [splitHashProperty]. */
val Location.hashProperty: Property<String> get() = windowLocationHash
private val windowLocationHash: Property<String> = window.location.bindToHash()

/** A Property for window.location.hash as a split array.  It is bound to [hashProperty]. */
val Location.splitHashProperty: Property<Array<String>> get() = splitWindowLocationHash
private val splitWindowLocationHash: Property<Array<String>> = windowLocationHash.bind({ it.split("_").toTypedArray() }, { it.joinToString("_") })

private fun Location.bindToHash(): Property<String> {
    val property: Property<String> = Property(hash)
    var updating = true // avoid triggering an onNext yet
    routerJQuery.on("hashchange") {
        if (!updating) {
            updating = true
            try {
                property.set(hash)
            } finally {
                updating = false
            }
        }
    }
    property.onNext { newValue ->
        if (!updating) {
            updating = true
            try {
                routerJQuery.trigger("hashchange")
            } finally {
                updating = false
            }
        }
    }
    // enable the functionality
    updating = false
    return property
}

@Deprecated("If runNow is true, use window.location.splitHashProperty.onNext(listener) instead",
        replaceWith = ReplaceWith("window.location.splitHashProperty.onNext(listener)"),
        level = DeprecationLevel.WARNING)
fun registerHashChangeListener(runNow:Boolean = true, listener:(Array<String>) -> Unit) {
    if (runNow) {
        window.location.splitHashProperty.onNext { listener(it) }
    } else {
        routerJQuery.on("hashchange") {
            listener(window.location.splitHashProperty.get())
        }
    }
}
