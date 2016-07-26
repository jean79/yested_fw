package net.yested.core.html

import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.properties.ValidationStatus
import net.yested.core.utils.removeAllChildElements
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.removeClass


//TODO: this should be moved to bootstrap? or wrapped by bootstrap?

var YESTED_INPUT_INVALID_CLASS = "yestedInputInvalidClass"

fun HTMLElement.text(value: ReadOnlyProperty<String>) {
    val element = document.createElement("span") as HTMLSpanElement
    value.onNext {
        element.textContent = it
    }
    this.appendChild(element)
}

fun HTMLElement.textInput(
        value: Property<String>,
        className: ReadOnlyProperty<String>? = null,
        validation: ReadOnlyProperty<ValidationStatus>? = null) {

    val element = document.createElement("input") as HTMLInputElement

    element.type = "text"
    value.onNext {
        element.value = it
    }
    element.addEventListener("change", {
        value.set(element.value)
    }, false)
    element.addEventListener("keyup", {
        value.set(element.value)
    }, false)
    className?.onNext {
        element.className = it
    }

    validation?.let {
        validation.onNext {
            if (it.success) {
                element.removeClass(YESTED_INPUT_INVALID_CLASS)
            } else {
                element.addClass(YESTED_INPUT_INVALID_CLASS)
            }
        }
    }

    this.appendChild(element)

}

fun <T> HTMLElement.selectInput(
        selected: Property<T>,
        options: Property<List<T>>,
        render: HTMLElement.(T)->Unit) {

    val element = document.createElement("select") as HTMLSelectElement
    options.onNext {
        element.removeAllChildElements()
        it.forEachIndexed { index, item ->
            val option:HTMLOptionElement = document.createElement("option").asDynamic()
            option.value = "$index"
            if (item == selected.get()) {
                option.selected = true
            }
            option.render(item)
            element.appendChild(option)
        }
    }

    element.addEventListener("change", {
        val selectedIndex = parseInt(element.value)
        val selectedItem = options.get()[selectedIndex]
        selected.set(selectedItem)
    }, false)
    this.appendChild(element)
}
