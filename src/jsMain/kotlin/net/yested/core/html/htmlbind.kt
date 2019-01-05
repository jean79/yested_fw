package net.yested.core.html

import net.yested.core.properties.bind
import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.properties.map
import net.yested.core.properties.zip
import net.yested.core.utils.*
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

fun HTMLTextAreaElement.bind(property: Property<String>) {
    var updating = false
    property.onNext {
        if (!updating) {
            value = it
        }
    }
    addEventListener("change", { updating = true; property.set(value); updating = false }, false)
    addEventListener("keyup", { updating = true; property.set(value); updating = false }, false)
}

fun HTMLInputElement.bindChecked(checked: Property<Boolean>) {
    val element = this
    var updating = false
    checked.onNext {
        if (!updating) {
            element.checked = it
        }
    }
    addEventListener("change", { updating = true; checked.set(element.checked); updating = false }, false)
}

fun <T> HTMLSelectElement.bindMultiselect(selected: Property<List<T>>, options: ReadOnlyProperty<List<T>>, render: HTMLElement.(T)->Unit) {
    bindMultiselect(selected, options, { selected.set(it) }, render)
}

fun <T> HTMLSelectElement.bindMultiselect(selected: ReadOnlyProperty<List<T>>, options: ReadOnlyProperty<List<T>>, onSelect: (List<T>) -> Unit, render: HTMLElement.(T)->Unit) {
    val selectElement = this
    options.onNext {
        removeAllChildElements()
        it.forEachIndexed { index, item ->
            val option: HTMLOptionElement = document.createElement("option").asDynamic()
            option.value = "$index"
            option.render(item)
            appendChild(option)
        }
    }
    var updating = false
    selected.zip(options).onNext { (selectedList, options) ->
        if (!updating) {
            options.forEachIndexed { index, option ->
                if (index < selectElement.options.length) {
                    (selectElement.options.get(index) as HTMLOptionElement).selected = selectedList.contains(option)
                }
            }
        }
    }
    addEventListener("change", {
        val selectOptions = this.options
        val selectedValues = (1..selectOptions.length)
                .map { selectOptions[it - 1] }
                .filter { (it as HTMLOptionElement).selected }
                .map { (it as HTMLOptionElement).value }
                .map { options.get()[it.toInt()] }
        updating = true
        onSelect.invoke(selectedValues)
        updating = false
    }, false)
}

fun <T> HTMLSelectElement.bind(selected: Property<T>, options: ReadOnlyProperty<List<T>>, render: HTMLElement.(T)->Unit) {
    @Suppress("UNCHECKED_CAST") // T is allowed to be nullable or not-nullable.
    val multipleSelected = selected.bind({ if (it == null) emptyList<T>() else listOf(it) }, { it.firstOrNull() as T })
    bindMultiselect(multipleSelected, options, render)
}

fun <T> HTMLSelectElement.bind(selected: ReadOnlyProperty<T>, options: ReadOnlyProperty<List<T>>, onSelect: (T) -> Unit, render: HTMLElement.(T)->Unit) {
    val multiSelected: ReadOnlyProperty<List<T>> = selected.map { if (it == null) emptyList() else listOf(it) }
    @Suppress("UNCHECKED_CAST") // T is allowed to be nullable or not-nullable.
    bindMultiselect(multiSelected, options, { onSelect(it.firstOrNull() as T) }, render)
}

fun HTMLElement.setClassPresence(className: String, present: ReadOnlyProperty<Boolean>) {
    setClassPresence(className, present, true)
}

fun <T> HTMLElement.setClassPresence(className: String, property: ReadOnlyProperty<T>, presentValue: T) {
    property.onNext {
        if (it == presentValue) addClass2(className) else removeClass2(className)
    }
}

fun HTMLButtonElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLInputElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLTextAreaElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLSelectElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLFieldSetElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLInputElement.setReadOnly(property: ReadOnlyProperty<Boolean>) {
    property.onNext { readOnly = it }
}

fun HTMLTextAreaElement.setReadOnly(property: ReadOnlyProperty<Boolean>) {
    property.onNext { readOnly = it }
}

/** This exists because Kotlin's ArrayList calls throwCCE() if a non-null Element doesn't extend "Any". */
private data class ElementWrapper(val element: Element?)

private fun HTMLCollection.toWrapperList(): List<ElementWrapper> {
    return (0..(this.length - 1)).map { ElementWrapper(item(it)) }
}

fun <C: HTMLElement,T> C.repeatLive(orderedData: ReadOnlyProperty<Iterable<T>?>, effect: BiDirectionEffect = NoEffect, itemInit: C.(T) -> Unit) {
    return repeatLive(orderedData, effect, { _, item -> itemInit(item) })
}

fun <C: HTMLElement,T> C.repeatLive(orderedData: ReadOnlyProperty<Iterable<T>?>, effect: BiDirectionEffect = NoEffect, itemInit: C.(Int, T) -> Unit) {
    val containerElement = this
    val itemsWithoutDelays = mutableListOf<List<ElementWrapper>>()
    var operableList : DomOperableList<C,T>? = null
    var elementAfter: HTMLElement? = null

    orderedData.onNext { values ->
        val operableListSnapshot = operableList
        if (values == null) {
            itemsWithoutDelays.flatten().forEach {
                if (it?.element?.parentElement == containerElement) {
                    containerElement.removeChild(it.element)
                }
            }
            itemsWithoutDelays.clear()
            operableList = null
        } else if (operableListSnapshot == null) {
            itemsWithoutDelays.flatten().forEach {
                if (it?.element?.parentElement == containerElement) {
                    containerElement.removeChild(it.element)
                }
            }
            itemsWithoutDelays.clear()
            val domOperableList = DomOperableList(values.toMutableList(), itemsWithoutDelays, containerElement, effect, elementAfter, itemInit)
            values.forEachIndexed { index, item ->
                domOperableList.addItemToContainer(containerElement, index, item, itemsWithoutDelays, elementAfter)
            }
            operableList = domOperableList
        } else {
            operableListSnapshot.reconcileTo(values.toList())
        }
    }
    elementAfter = span() // create a <span/> to clearly indicate where to insert new elements.
    operableList?.elementAfter = elementAfter
}

private class DomOperableList<C : HTMLElement,T>(
        initialData: MutableList<T>,
        val itemsWithoutDelays: MutableList<List<ElementWrapper>>,
        val container: C,
        val effect: BiDirectionEffect,
        var elementAfter: HTMLElement? = null,
        val itemInit: C.(Int, T) -> Unit) : InMemoryOperableList<T>(initialData) {
    override fun add(index: Int, item: T) {
        addItemToContainer(container, index, item, itemsWithoutDelays, elementAfter).forEach { if (it.element is HTMLElement) effect.applyIn(it.element) }
        super.add(index, item)
    }

    override fun removeAt(index: Int): T {
        val elementsForIndex = itemsWithoutDelays.removeAt(index)
        elementsForIndex.forEach {
            if (it.element is HTMLElement) {
                effect.applyOut(it.element) {
                    if (it.element.parentElement == container) {
                        container.removeChild(it.element)
                    }
                }
            } else if (it.element?.parentElement == container) {
                container.removeChild(it.element)
            }
        }
        return super.removeAt(index)
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        val item = removeAt(fromIndex)
        add(toIndex, item)
    }

    fun addItemToContainer(container: C, index: Int, item: T, itemsWithoutDelays: MutableList<List<ElementWrapper>>, elementAfter: HTMLElement?): List<ElementWrapper> {
        val nextElement = if (index < itemsWithoutDelays.size) itemsWithoutDelays.get(index).firstOrNull()?.element else elementAfter
        val childrenBefore = container.children.toWrapperList()
        container.itemInit(index, item)
        val childrenLater = container.children.toWrapperList()
        val newChildren = childrenLater.filterNot { childrenBefore.contains(it) }
        if (nextElement != null && nextElement.parentElement == container) {
            newChildren.forEach { it.element?.let { container.insertBefore(it, nextElement) } }
        }
        itemsWithoutDelays.add(index, newChildren)
        return newChildren
    }
}

/**
 * Bind table content to a Property<Iterable<T>>.  The index and value are provided to itemInit.
 * Example:<pre>
 *   table {
 *       thead {
 *           th { appendText("Name") }
 *           th { appendText("Value") }
 *       }
 *       tbody(myData, effect = Collapse()) { index, item ->
 *           tr { className = if (index % 2 == 0) "even" else "odd"
 *               td { appendText(item.name) }
 *               td { appendText(item.value) }
 *           }
 *       }
 *   }
 * </pre>
 */
fun <T> HTMLTableElement.tbody(orderedData: ReadOnlyProperty<Iterable<T>?>, effect: BiDirectionEffect = NoEffect,
                               itemInit: HTMLTableSectionElement.(Int, T) -> Unit) {
    tbody { repeatLive(orderedData, effect, itemInit) }
}

/**
 * Bind table content to a Property<Iterable<T>>.  The value is provided to itemInit.
 * Example:<pre>
 *   table {
 *       thead {
 *           th { appendText("Name") }
 *           th { appendText("Value") }
 *       }
 *       tbody(myData, effect = Collapse()) { item ->
 *           tr { className = if (index % 2 == 0) "even" else "odd"
 *               td { appendText(item.name) }
 *               td { appendText(item.value) }
 *           }
 *       }
 *   }
 * </pre>
 */
fun <T> HTMLTableElement.tbody(orderedData: ReadOnlyProperty<Iterable<T>?>, effect: BiDirectionEffect = NoEffect,
                               itemInit: HTMLTableSectionElement.(T) -> Unit): HTMLTableSectionElement {
    return tbody { repeatLive(orderedData, effect, itemInit) }
}
