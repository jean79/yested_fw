package net.yested.ext.bootstrap3

import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.properties.toProperty
import net.yested.core.utils.removeAllChildElements
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLSpanElement
import kotlin.browser.document

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

        id: String? = null) {

    val element = document.createElement("input") as HTMLInputElement

    id?.let { element.id = id }
    element.className = "form-control"
    element.type = "text"
    value.onNext {
        element.value = it
    }
    element.addEventListener("change", { value.set(element.value)  }, false)
    element.addEventListener("keyup", { value.set(element.value) }, false)
    disabled.onNext { element.disabled = it }
    readonly.onNext { element.readOnly = it }
    this.appendChild(element)

}

fun <T> HTMLElement.selectInput(
        selected: Property<List<T>>,
        options: Property<List<T>>,
        disabled: ReadOnlyProperty<Boolean> = false.toProperty(),
        multiple: Boolean,
        render: HTMLElement.(T)->Unit) {

    val element = document.createElement("select") as HTMLSelectElement
    element.className = "form-control"
    element.multiple = multiple
    options.onNext {
        element.removeAllChildElements()
        it.forEachIndexed { index, item ->
            val option: HTMLOptionElement = document.createElement("option").asDynamic()
            option.value = "$index"
            if (item == selected.get().filter { it == item}.isNotEmpty()) {
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

    val multipleSelected = Property<List<T>>(listOf())

    var changingHere = false
    selected.onNext {
        changingHere = true
        multipleSelected.set(listOf(it))
        changingHere = false
    }
    multipleSelected.onNext {
        if (!changingHere) {
            selected.set(it.first())
        }
    }

    selectInput(
            selected = multipleSelected,
            options = options,
            multiple = false,
            disabled = disabled,
            render = render)

}