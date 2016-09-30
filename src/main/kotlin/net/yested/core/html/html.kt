package net.yested.core.html

import org.w3c.dom.*
import kotlin.browser.document
import kotlin.dom.appendText

fun <T : HTMLElement> tag(parent: Element, tagName: String, init:(T.()->Unit)? = null): T {
    val element:T = document.createElement(tagName).asDynamic()
    parent.appendChild(element)
    init?.let { element.init() }
    return element
}

fun HTMLElement.div(init:(HTMLDivElement.()->Unit)? = null) = tag(this, tagName = "div", init = init)
fun HTMLElement.p(init:(HTMLParagraphElement.()->Unit)? = null) = tag(this, tagName = "p", init = init)
fun HTMLElement.nav(init:(HTMLDivElement.()->Unit)? = null) = tag(this, tagName = "nav", init = init)
fun HTMLElement.span(init:(HTMLSpanElement.()->Unit)? = null) = tag(this, tagName = "span", init = init)
fun HTMLElement.table(init:(HTMLTableElement.()->Unit)? = null) = tag(this, tagName = "table", init = init)
fun HTMLElement.tr(init:(HTMLTableRowElement.()->Unit)? = null) = tag(this, tagName = "tr", init = init)
fun HTMLElement.td(init:(HTMLTableCellElement.()->Unit)? = null) = tag(this, tagName = "td", init = init)
fun HTMLElement.th(init:(HTMLTableHeaderCellElement.()->Unit)? = null) = tag(this, tagName = "th", init = init)
fun HTMLElement.thead(init:(HTMLTableSectionElement.()->Unit)? = null) = tag(this, tagName = "thead", init = init)
fun HTMLElement.tbody(init:(HTMLTableSectionElement.()->Unit)? = null) = tag(this, tagName = "tbody", init = init)
fun HTMLElement.a(init:(HTMLAnchorElement.()->Unit)? = null) = tag(this, tagName = "a", init = init)
fun HTMLElement.select(init:(HTMLSelectElement.()->Unit)? = null) = tag(this, tagName = "select", init = init)
fun HTMLElement.ul(init:(HTMLUListElement.()->Unit)? = null) = tag(this, tagName = "ul", init = init)
fun HTMLElement.li(init:(HTMLLIElement.()->Unit)? = null) = tag(this, tagName = "li", init = init)
fun HTMLElement.h1(init:(HTMLHeadingElement.()->Unit)? = null) = tag(this, tagName = "h1", init = init)
fun HTMLElement.h2(init:(HTMLHeadingElement.()->Unit)? = null) = tag(this, tagName = "h2", init = init)
fun HTMLElement.h3(init:(HTMLHeadingElement.()->Unit)? = null) = tag(this, tagName = "h3", init = init)
fun HTMLElement.h4(init:(HTMLHeadingElement.()->Unit)? = null) = tag(this, tagName = "h4", init = init)
fun HTMLElement.h5(init:(HTMLHeadingElement.()->Unit)? = null) = tag(this, tagName = "h5", init = init)
fun HTMLElement.h6(init:(HTMLHeadingElement.()->Unit)? = null) = tag(this, tagName = "h6", init = init)
fun HTMLElement.nbsp(count: Int = 1) = (1..count).forEach { appendText(Typography.nbsp.toString()) }
fun HTMLElement.hr() = tag<HTMLHRElement>(this, tagName = "hr")
fun HTMLElement.strong(init:(HTMLSpanElement.()->Unit)? = null) = tag(this, tagName = "strong", init = init)
fun HTMLElement.button(init:(HTMLButtonElement.()->Unit)? = null) = tag(this, tagName = "button", init = init)
fun HTMLElement.form(init:(HTMLFormElement.()->Unit)? = null) = tag(this, tagName = "form", init = init)
fun HTMLElement.label(init:(HTMLLabelElement.()->Unit)? = null) = tag(this, tagName = "label", init = init)

enum class Align(val code:String) {
   LEFT("left"),
   CENTER("center"),
   RIGHT("right")
}
