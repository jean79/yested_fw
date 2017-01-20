package net.yested.ext.bootstrap3

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
        init: (HTMLInputElement.() -> Unit)? = null) {

    val element = document.createElement("input") as HTMLInputElement

    var updating = false
    id?.let { element.id = id }
    element.className = "form-control"
    element.addClass(inputTypeClass)
    element.type = "text"
    value.onNext {
        if (!updating) {
            element.value = it
        }
    }
    element.addEventListener("change", { updating = true; value.set(element.value); updating = false }, false)
    element.addEventListener("keyup", { updating = true; value.set(element.value); updating = false }, false)
    disabled.onNext { element.disabled = it }
    readonly.onNext { element.readOnly = it }
    if (init != null) element.init()
    this.appendChild(element)
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
    options.onNext {
        element.removeAllChildElements()
        it.forEachIndexed { index, item ->
            val option: HTMLOptionElement = document.createElement("option").asDynamic()
            option.value = "$index"
            if (selected.get().filter { it == item}.isNotEmpty()) {
                option.selected = true
            }
            option.render(item)
            element.appendChild(option)
        }
    }

    disabled.onNext { element.disabled = it }

    element.addEventListener("change", {

        val selectOptions = element.options
        val selectedValues = (1..selectOptions.length)
                .map { selectOptions[it - 1] }
                .filter { it.asDynamic().selected }
                .map { it.asDynamic().value }
                .map { options.get()[parseInt(it)] }

        selected.set(selectedValues)

    }, false)
    this.appendChild(element)
}

fun <T> HTMLElement.singleSelectInput(
        selected: Property<T>,
        options: Property<List<T>>,
        disabled: ReadOnlyProperty<Boolean> = false.toProperty(),
        render: HTMLElement.(T)->Unit) {

    val multipleSelected = selected.mapBidirectionally({ if (it == null) emptyList() else listOf(it) }, { it.firstOrNull() as T })

    selectInput(
            selected = multipleSelected,
            options = options,
            multiple = false,
            disabled = disabled,
            render = render)

}

fun HTMLElement.intInput(value: Property<Int?>,
        disabled: ReadOnlyProperty<Boolean> = false.toProperty(),
        readonly: ReadOnlyProperty<Boolean> = false.toProperty(),
        id: String? = null,
        init: (HTMLInputElement.() -> Unit)? = null) {
    val textValue = value.mapBidirectionally(
            transform = { if (it == null) "" else it.toString() },
            reverse = { if (!it.isEmpty()) parseInt(it) else null })
    textInput(textValue, disabled, readonly, id, inputTypeClass = "number int") {
        type = "number"; step = "1"
        if (init != null) init()
    }
}
