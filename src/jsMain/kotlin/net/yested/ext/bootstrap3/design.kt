package net.yested.ext.bootstrap3

import net.yested.core.html.div
import net.yested.core.html.span
import net.yested.core.properties.ReadOnlyProperty
import org.w3c.dom.HTMLElement

fun HTMLElement.jumbotron(init: HTMLElement.()->Unit) {
    div { className = "jumbotron"
        init()
    }
}

fun HTMLElement.glyphicon(icon: String) {
    span { className = "glyphicon glyphicon-$icon" }
}

fun HTMLElement.glyphicon(icon: ReadOnlyProperty<String?>) {
    span {
        icon.onNext { className = it?.let { "glyphicon glyphicon-$it" } ?: "" }
    }
}