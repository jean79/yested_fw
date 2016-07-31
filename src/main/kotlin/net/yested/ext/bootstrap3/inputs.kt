package net.yested.ext.bootstrap3

import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.utils.removeAllChildElements
import org.w3c.dom.*
import kotlin.browser.document

fun HTMLElement.text(value: ReadOnlyProperty<String>) {
    val element = document.createElement("span") as HTMLSpanElement
    value.onNext {
        element.textContent = it
    }
    this.appendChild(element)
}

fun HTMLElement.textInput(value: Property<String>, id: String? = null) {

    val element = document.createElement("input") as HTMLInputElement

    id?.let { element.id = id }
    element.className = "form-control"
    element.type = "text"
    value.onNext {
        element.value = it
    }
    element.addEventListener("change", { value.set(element.value)  }, false)
    element.addEventListener("keyup", { value.set(element.value) }, false)

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
            val option: HTMLOptionElement = document.createElement("option").asDynamic()
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
