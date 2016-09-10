package net.yested.ext.bootstrap3

import net.yested.core.html.div
import net.yested.core.html.hr
import net.yested.core.utils.with
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.browser.document

enum class ContainerWidth(val code: String) {
    Fixed("container"),
    Fluid("container-fluid")
}

fun HTMLElement.container(
        width: ContainerWidth = ContainerWidth.Fixed,
        init: HTMLDivElement.()->Unit) {
    div { className = "${width.code}"
        init()
    }
}

fun HTMLElement.pageHeader(init: HTMLDivElement.() -> Unit) {
    div { className = "page-header"
        init()
    }
}

class PageContext(val element: HTMLElement, val layout: ContainerWidth) {
    fun header(init: HTMLDivElement.() -> Unit) {
        element.pageHeader(init)
    }

    fun navbar(
            position: NavbarCompletePosition = NavbarCompletePosition.Top,
            inverted: Boolean = false,
            init: NavbarContext.()->Unit) {
        element.navbar(position, inverted, init)
    }

    fun content(init: HTMLDivElement.() -> Unit) {
        element.div {
            "class"..layout.code
            init()
        }
   }

    fun footer(init: HTMLDivElement.() -> Unit) {
        element.container(ContainerWidth.Fixed) {
            hr()
            init()
        }
   }
}

fun HTMLElement.page(layout: ContainerWidth = ContainerWidth.Fixed, init: PageContext.() -> Unit) {
    className = layout.code
    PageContext(this, layout).init()
}

fun page(placeholderElementId:String, layout: ContainerWidth = ContainerWidth.Fixed, init: PageContext.() -> Unit) {
    document.getElementById(placeholderElementId) as HTMLElement with {
        className = layout.code
        PageContext(this, layout).init()
    }
}
