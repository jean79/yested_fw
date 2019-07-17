package net.yested.ext.jquery

import net.yested.core.properties.Property
import net.yested.core.properties.bind
import org.w3c.dom.History
import org.w3c.dom.Location
import kotlin.browser.document
import kotlin.browser.window

internal val jQueryWindow: JQueryWindow = yestedJQuery(window)

/** A Property for window.location.hash.  It is bound to [splitHashProperty]. */
val Location.hashProperty: Property<String> get() = windowLocationHash
private val windowLocationHash: Property<String> = window.location.bindToHash()

/** A Property for window.location.hash as a split array.  It is bound to [hashProperty]. */
val Location.splitHashProperty: Property<Array<String>> get() = splitWindowLocationHash
private val splitWindowLocationHash: Property<Array<String>> = windowLocationHash.bind({ it.split("/").toTypedArray() }, { it.joinToString("/") })

private fun Location.bindToHash(): Property<String> {
    val property: Property<String> = Property(hash)
    var updating = true // avoid triggering an onNext yet
    jQueryWindow.on("hashchange") { _, _ ->
        if (!updating) {
            updating = true
            try {
                property.set(hash)
            } finally {
                updating = false
            }
        }
    }
    property.onNext { _ ->
        if (!updating) {
            updating = true
            try {
                jQueryWindow.trigger("hashchange")
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
        jQueryWindow.on("hashchange") { _, _ ->
            listener(window.location.splitHashProperty.get())
        }
    }
}

fun History.backToHash(hashUrl: String?) {
    if (historyDestinationBack != hashUrl) {
        console.info("Going back to $hashUrl")
    }
    historyDestinationBack = hashUrl
    backToDestination()
}

private fun History.backToDestination() {
    val hashUrl = historyDestinationBack
    if (window.location.hash == "") {
        console.info("got to the main entry-point page.  Assuming it's close enough to '$hashUrl'")
        historyDestinationBack = null
    } else if (hashUrl == null) {
        back()
    } else if (historyDestinationBack == windowLocationHash.get()) {
        historyDestinationBack = null
    } else if (this.length <= 1) {
        historyDestinationBack = null
        replaceState(null, document.title, hashUrl)
        console.info("got to the beginning of browser history going to $hashUrl")
        windowLocationHash.set(hashUrl)
    } else {
        window.setTimeout({
            val destinationBack = window.history.destinationBack
            if (destinationBack != null) {
                backToDestination()
            }
        }, 100)
        back()
    }
}

/**
 * A place to store the current destination going back, outside of the [backToHash] method.
 * It is only public to be accessible from tests in other projects.
 */
val History.destinationBack: String? get() = historyDestinationBack
private var historyDestinationBack: String? = null

/** Clears the history going back.  This helps if there's some sort of redirect to avoid an infinite loop. */
fun History.clearDestinationBack() {
    historyDestinationBack = null
}
