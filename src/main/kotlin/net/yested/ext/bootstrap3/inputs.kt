package net.yested.ext.bootstrap3

import net.yested.core.html.bind
import net.yested.core.html.bindMultiselect
import net.yested.core.html.setDisabled
import net.yested.core.html.setReadOnly
import net.yested.core.properties.*
import net.yested.core.utils.removeAllChildElements
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLSpanElement
import kotlin.browser.document
import kotlin.dom.addClass

fun HTMLElement.text(value: ReadOnlyProperty<String>) {
    val element = document.createElement("span") as HTMLSpanElement
    value.onNext {
        element.textContent = it
    }
    this.appendChild(element)
}

fun HTMLElement.textInput(
        value: Property<String>,
        disabled: ReadOnlyProperty<Boolean> = false.toProperty(),
        readonly: ReadOnlyProperty<Boolean> = false.toProperty(),
        id: String? = null,
        inputTypeClass: String = "text",
        init: (HTMLInputElement.() -> Unit)? = null): HTMLInputElement {

    val element = document.createElement("input") as HTMLInputElement

    id?.let { element.id = id }
    element.className = "form-control $inputTypeClass"
    element.type = "text"
    element.bind(value)
    element.setDisabled(disabled)
    element.setReadOnly(readonly)
    if (init != null) element.init()
    this.appendChild(element)
    return element
}

fun <T> HTMLElement.selectInput(
        selected: Property<List<T>>,
        options: Property<List<T>>,
        disabled: ReadOnlyProperty<Boolean> = false.toProperty(),
        multiple: Boolean,
        size: Size = Size.Default,
        render: HTMLElement.(T)->Unit) {

    val element = document.createElement("select") as HTMLSelectElement
    element.className = "form-control input-${size.code}"
    element.multiple = multiple
    element.bindMultiselect(selected, options, render)
    element.setDisabled(disabled)
    this.appendChild(element)
}

fun <T> HTMLElement.singleSelectInput(
        selected: Property<T>,
        options: Property<List<T>>,
        disabled: ReadOnlyProperty<Boolean> = false.toProperty(),
        render: HTMLElement.(T)->Unit) {

    val element = document.createElement("select") as HTMLSelectElement
    element.className = "form-control input-${Size.Default.code}"
    element.multiple = false
    element.bind(selected, options, render)
    element.setDisabled(disabled)
    this.appendChild(element)
}

fun HTMLElement.intInput(value: Property<Int?>,
        disabled: ReadOnlyProperty<Boolean> = false.toProperty(),
        readonly: ReadOnlyProperty<Boolean> = false.toProperty(),
        id: String? = null,
        init: (HTMLInputElement.() -> Unit)? = null) {
    val textValue = value.bind(
            transform = { if (it == null) "" else it.toString() },
            reverse = { if (!it.isEmpty()) parseInt(it) else null })
    textInput(textValue, disabled, readonly, id, inputTypeClass = "number int") {
        type = "number"; step = "1"
        if (init != null) init()
    }
}
