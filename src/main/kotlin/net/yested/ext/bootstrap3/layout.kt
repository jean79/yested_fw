package net.yested.ext.bootstrap3

import net.yested.core.html.div
import net.yested.core.html.hr
import net.yested.core.utils.with
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.browser.document

enum class ContainerWidth(val code: String) {
    /** Makes each item take up the full width. */
    Fixed("container"),
    /** Allows items to share rows. */
    Fluid("container-fluid")
}

fun HTMLElement.container(
        width: ContainerWidth = ContainerWidth.Fixed,
        init: HTMLDivElement.()->Unit) {
    div { className = width.code
        init()
    }
}

fun HTMLElement.pageHeader(init: HTMLDivElement.() -> Unit) {
    div { className = "page-header"
        init()
    }
}

class PageContext(val element: HTMLElement) {
    fun navbar(
            position: NavbarCompletePosition = NavbarCompletePosition.Top,
            inverted: Boolean = false,
            containerWidth: ContainerWidth = ContainerWidth.Fixed,
            init: NavbarContext.()->Unit) {
        element.navbar(position, inverted, containerWidth, init)
    }

    fun header(init: HTMLDivElement.() -> Unit) {
        element.pageHeader(init)
    }

    fun content(layout: ContainerWidth = ContainerWidth.Fixed, init: HTMLDivElement.() -> Unit) {
        element.container(layout) {
            init()
        }
   }

    fun footer(init: HTMLDivElement.() -> Unit) {
        element.div { className = ContainerWidth.Fixed.code
            hr()
            init()
        }
   }
}

fun HTMLElement.page(layout: ContainerWidth? = null, init: PageContext.() -> Unit) {
    if (layout != null) className = layout.code
    PageContext(this).init()
}

fun page(placeholderElementId:String, layout: ContainerWidth? = null, init: PageContext.() -> Unit) {
    document.getElementById(placeholderElementId) as HTMLElement with {
        page(layout, init)
    }
}
