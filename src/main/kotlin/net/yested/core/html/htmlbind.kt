package net.yested.core.html

import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.properties.bind
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

fun <T> HTMLSelectElement.bindMultiselect(selected: Property<List<T>>, options: Property<List<T>>, render: HTMLElement.(T)->Unit) {
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
    selected.onNext { selectedList ->
        if (!updating) {
            options.get().forEachIndexed { index, option ->
                (selectElement.options.get(index) as HTMLOptionElement).selected = selectedList.contains(option)
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
        selected.set(selectedValues)
        updating = false
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

fun HTMLFieldSetElement.setDisabled(property: ReadOnlyProperty<Boolean>) {
    property.onNext { disabled = it }
}

fun HTMLInputElement.setReadOnly(property: ReadOnlyProperty<Boolean>) {
    property.onNext { readOnly = it }
}

fun HTMLCollection.toList(): List<HTMLElement> {
    return (0..(this.length - 1)).map { item(it)!! as HTMLElement }
}

internal abstract class DynamicHTMLCollection<C : HTMLElement,I : HTMLElement,T>(
        val orderedData: ReadOnlyProperty<Iterable<T>?>, val effect: BiDirectionEffect) {
    abstract fun addItemViaContext(container: C, index: Int, item: T)
    abstract fun addItemToDom(container: C, before: HTMLElement?, init: (I.() -> Unit)?): I
    abstract fun addItemViaContextWithAnimation(container: C, index: Int, item: T, itemsWithoutDelays: MutableList<HTMLElement>)

    fun invoke(containerElement: C) {
        var operableList : OperableList<T>? = null

        orderedData.onNext { values ->
            val operableListSnapshot = operableList
            if (values == null) {
                containerElement.removeAllChildElements()
                operableList = null
            } else if (operableListSnapshot == null) {
                containerElement.removeAllChildElements()
                values.forEachIndexed { index, item ->
                    addItemViaContext(containerElement, index, item)
                }
                operableList = DomOperableList<C,I,T>(this, values.toMutableList(), containerElement, effect)
            } else {
                operableListSnapshot.reconcileTo(values.toList())
            }
        }
    }

    fun addItemWithAnimation(container: C, index: Int, itemsWithoutDelays: MutableList<HTMLElement>, init: (I.() -> Unit)?): I {
        val nextRow = if (index < itemsWithoutDelays.size) itemsWithoutDelays.get(index) else null
        val newRow = addItemToDom(container, nextRow, init)
        effect.applyIn(newRow)
        itemsWithoutDelays.add(index, newRow)
        return newRow
    }
}

internal class DomOperableList<C : HTMLElement,I : HTMLElement,T>(
        val dynamicHTMLCollection: DynamicHTMLCollection<C,I,T>,
        initialData: MutableList<T>, val container: C, val effect: BiDirectionEffect) : InMemoryOperableList<T>(initialData) {
    private val itemsWithoutDelays: MutableList<HTMLElement> = container.children.toList().toMutableList()

    override fun add(index: Int, item: T) {
        dynamicHTMLCollection.addItemViaContextWithAnimation(container, index, item, itemsWithoutDelays)
        super.add(index, item)
    }

    override fun removeAt(index: Int): T {
        val row = itemsWithoutDelays.removeAt(index)
        effect.applyOut(row) {
            container.removeChild(row)
        }
        return super.removeAt(index)
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        val item = removeAt(fromIndex)
        add(toIndex, item)
    }
}

/**
 * Bind table content to a Property<Iterable<T>>.  The index and value are provided to tbodyItemInit.
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
                               itemInit: TableItemContext.(Int, T) -> Unit) {
    tbody { DynamicTBody(orderedData, effect, itemInit).invoke(this) }
}

/**
 * Bind table content to a Property<Iterable<T>>.  The value is provided to tbodyItemInit.
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
                               itemInit: TableItemContext.(T) -> Unit) {
    return tbody(orderedData, effect, { index, item -> itemInit(item) })
}

class TableItemContext(private val addItemToDom: ((HTMLTableRowElement.() -> Unit)?)->HTMLTableRowElement) {
    fun tr(init:(HTMLTableRowElement.()->Unit)? = null): HTMLTableRowElement = addItemToDom(init)
}

internal class DynamicTBody<T>(orderedData: ReadOnlyProperty<Iterable<T>?>, effect: BiDirectionEffect, val itemInit: TableItemContext.(Int, T) -> Unit)
    : DynamicHTMLCollection<HTMLTableSectionElement,HTMLTableRowElement,T>(orderedData, effect) {

    override fun addItemViaContext(container: HTMLTableSectionElement, index: Int, item: T) {
        TableItemContext({ init -> addItemToDom(container, null, init) }).itemInit(index, item)
    }

    override fun addItemToDom(container: HTMLTableSectionElement, before: HTMLElement?, init: (HTMLTableRowElement.() -> Unit)?): HTMLTableRowElement {
        return container.tr(before = before, init = init)
    }

    override fun addItemViaContextWithAnimation(container: HTMLTableSectionElement, index: Int, item: T, itemsWithoutDelays: MutableList<HTMLElement>) {
        TableItemContext({ init -> addItemWithAnimation(container, index, itemsWithoutDelays, init) }).itemInit(index, item)
    }
}

/**
 * Bind ul to a Property<Iterable<T>>.  The index and value are provided to itemInit.
 * Example:<pre>
 *   ul(myData, effect = Collapse()) { index, item ->
 *       li { className = if (index % 2 == 0) "even" else "odd"
 *           appendText(item.name)
 *       }
 *   }
 * </pre>
 */
fun <T> HTMLDivElement.ul(orderedData: ReadOnlyProperty<Iterable<T>?>, effect: BiDirectionEffect = NoEffect,
                          itemInit: ListItemContext.(Int, T) -> Unit) {
    ul { DynamicUList(orderedData, effect, itemInit).invoke(this) }
}

/**
 * Bind ul to a Property<Iterable<T>>.  The index and value are provided to itemInit.
 * Example:<pre>
 *   ul(myData, effect = Collapse()) { item ->
 *       li { appendText(item.name) }
 *   }
 * </pre>
 */
fun <T> HTMLDivElement.ul(orderedData: ReadOnlyProperty<Iterable<T>?>, effect: BiDirectionEffect = NoEffect,
                          itemInit: ListItemContext.(T) -> Unit) {
    return ul(orderedData, effect, { _, item -> itemInit(item) })
}

class ListItemContext(private val addItemToDom: ((HTMLLIElement.() -> Unit)?)->HTMLLIElement) {
    fun li(init:(HTMLLIElement.()->Unit)? = null): HTMLLIElement = addItemToDom(init)
}

internal class DynamicUList<T>(orderedData: ReadOnlyProperty<Iterable<T>?>, effect: BiDirectionEffect, val itemInit: ListItemContext.(Int, T) -> Unit)
    : DynamicHTMLCollection<HTMLUListElement,HTMLLIElement,T>(orderedData, effect) {

    override fun addItemViaContext(container: HTMLUListElement, index: Int, item: T) {
        ListItemContext({ init -> addItemToDom(container, null, init) }).itemInit(index, item)
    }

    override fun addItemToDom(container: HTMLUListElement, before: HTMLElement?, init: (HTMLLIElement.() -> Unit)?): HTMLLIElement {
        return container.li(before, init)
    }

    override fun addItemViaContextWithAnimation(container: HTMLUListElement, index: Int, item: T, itemsWithoutDelays: MutableList<HTMLElement>) {
        ListItemContext({ init -> addItemWithAnimation(container, index, itemsWithoutDelays, init) }).itemInit(index, item)
    }
}
