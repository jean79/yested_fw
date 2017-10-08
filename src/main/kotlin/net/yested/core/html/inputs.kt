package net.yested.core.html

import net.yested.core.properties.Property
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document

/**
 * HTML input.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 8/4/17
 * Time: 10:35 PM
 */


/**
 * A checkbox.
 * @see [setDisabled]
 * @see [setReadOnly]
 */
fun HTMLElement.checkbox(
        checked: Property<Boolean>,
        name: String? = null,
        value: String? = null,
        id: String? = null,
        init: (HTMLInputElement.() -> Unit)? = null): HTMLInputElement {
    val element = document.createElement("input") as HTMLInputElement
    id?.let { element.id = id }
    element.type = "checkbox"
    element.name = name ?: ""
    element.value = value ?: ""
    element.bindChecked(checked)
    if (init != null) element.init()
    this.appendChild(element)
    return element
}
