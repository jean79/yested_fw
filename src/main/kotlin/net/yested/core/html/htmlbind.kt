package net.yested.core.html

import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.properties.bind
import net.yested.core.utils.removeAllChildElements
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.removeClass

/**
 * Property binding to HTML elements.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 2/2/17
 * Time: 11:00 PM
 */
fun HTMLInputElement.bind(property: Property<String>) {
    var updating = false
    property.onNext {
        if (!updating) {
            value = it
        }
    }
    addEventListener("change", { updating = true; property.set(value); updating = false }, false)
    addEventListener("keyup", { updating = true; property.set(value); updating = false }, false)
}

fun <T> HTMLSelectElement.bindMultiselect(selected: Property<List<T>>, options: Property<List<T>>, render: HTMLElement.(T)->Unit) {
    options.onNext {
        removeAllChildElements()
        it.forEachIndexed { index, item ->
            val option: HTMLOptionElement = document.createElement("option").asDynamic()
            option.value = "$index"
            if (selected.get().contains(item)) {
                option.selected = true
            }
            option.render(item)
            appendChild(option)
        }
    }
    addEventListener("change", {
        val selectOptions = this.options
        val selectedValues = (1..selectOptions.length)
                .map { selectOptions[it - 1] }
                .filter { it.asDynamic().selected }
                .map { it.asDynamic().value }
                .map { options.get()[parseInt(it)] }

        selected.set(selectedValues)
    }, false)
}

fun <T> HTMLSelectElement.bind(selected: Property<T>, options: Property<List<T>>, render: HTMLElement.(T)->Unit) {
    val multipleSelected = selected.bind({ if (it == null) emptyList() else listOf(it) }, { it.firstOrNull() as T })
    bindMultiselect(multipleSelected, options, render)
}


fun HTMLElement.setClassPresence(className: String, present: ReadOnlyProperty<Boolean>) {
    present.onNext {
        if (it) addClass(className) else removeClass(className)
    }
}

fun HTMLButtonElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLInputElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLSelectElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLInputElement.setReadOnly(property: ReadOnlyProperty<Boolean>) {
    property.onNext { readOnly = it }
}
