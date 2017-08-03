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
    var containerElement: HTMLTableSectionElement? = null
    var operableList : TBodyOperableList<T>? = null

    orderedData.onNext { values ->
        val operableListSnapshot = operableList
        if (values == null) {
            containerElement?.let { removeChild(it) }
            containerElement = null
            operableList = null
        } else if (operableListSnapshot == null) {
            containerElement?.let { removeChild(it) }
            val element = tbody {
                val tbody = this
                values.forEachIndexed { index, item ->
                    TableItemContext({ init -> tbody.tr(init = init) }).itemInit(index, item)
                }
            }
            operableList = TBodyOperableList(values.toMutableList(), element, effect, itemInit)
        } else {
            operableListSnapshot.reconcileTo(values.toList())
        }
    }
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

class TableItemContext(private val itemFactory: ((HTMLTableRowElement.()->Unit)?)->HTMLTableRowElement) {
    fun tr(init:(HTMLTableRowElement.()->Unit)? = null): HTMLTableRowElement {
        return itemFactory.invoke(init)
    }
}

class TBodyOperableList<T>(initialData: MutableList<T>, val tbodyElement: HTMLTableSectionElement,
                           val effect: BiDirectionEffect,
                           val itemInit: TableItemContext.(Int, T)->Unit) : InMemoryOperableList<T>(initialData) {
    private val itemsWithoutDelays = tbodyElement.rows.toList().toMutableList()

    override fun add(index: Int, item: T) {
        val nextRow = if (index < itemsWithoutDelays.size) itemsWithoutDelays.get(index) else null
        TableItemContext({ init ->
            val newRow = tbodyElement.tr(before = nextRow, init = init)
            effect.applyIn(newRow)
            itemsWithoutDelays.add(index, newRow)
            newRow
        }).itemInit(index, item)
        super.add(index, item)
    }

    override fun removeAt(index: Int): T {
        val row = itemsWithoutDelays.removeAt(index)
        effect.applyOut(row) {
            tbodyElement.removeChild(row)
        }
        return super.removeAt(index)
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        val item = removeAt(fromIndex)
        add(toIndex, item)
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
    var containerElement: HTMLUListElement? = null
    var operableList: ULOperableList<T>? = null

    orderedData.onNext { values ->
        val operableListSnapshot = operableList
        if (values == null) {
            containerElement?.let { removeChild(it) }
            containerElement = null
            operableList = null
        } else if (operableListSnapshot == null) {
            containerElement?.let { removeChild(it) }
            val element = ul {
                val container = this
                values.forEachIndexed { index, item ->
                    ListItemContext({ init -> container.li(init = init) }).itemInit(index, item)
                }
            }
            containerElement = element
            operableList = ULOperableList(values.toMutableList(), element, effect, itemInit)
        } else {
            operableListSnapshot.reconcileTo(values.toList())
        }
    }
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

class ListItemContext(private val itemFactory: ((HTMLLIElement.()->Unit)?)->HTMLLIElement) {
    fun li(init:(HTMLLIElement.()->Unit)? = null): HTMLLIElement {
        return itemFactory.invoke(init)
    }
}

class ULOperableList<T>(initialData: MutableList<T>, val ulistElement: HTMLUListElement,
                        val effect: BiDirectionEffect,
                        val itemInit: ListItemContext.(Int, T)->Unit) : InMemoryOperableList<T>(initialData) {
    private val itemsWithoutDelays = ulistElement.children.toList().toMutableList()

    override fun add(index: Int, item: T) {
        val nextRow = if (index < itemsWithoutDelays.size) itemsWithoutDelays.get(index) else null
        ListItemContext({ init ->
            val newRow = ulistElement.li(before = nextRow, init = init)
            effect.applyIn(newRow)
            itemsWithoutDelays.add(index, newRow)
            newRow
        }).itemInit(index, item)
        super.add(index, item)
    }

    override fun removeAt(index: Int): T {
        val item = itemsWithoutDelays.removeAt(index)
        effect.applyOut(item) {
            ulistElement.removeChild(item)
        }
        return super.removeAt(index)
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        val item = removeAt(fromIndex)
        add(toIndex, item)
    }
}
