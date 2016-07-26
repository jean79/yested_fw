package net.yested.ext.bootstrap3

import net.yested.core.html.div
import org.w3c.dom.HTMLElement

enum class ContainerWidth(val code: String) {
    Fixed("container"),
    Fluid("container-fluid")
}

fun HTMLElement.container(
        width: ContainerWidth = ContainerWidth.Fixed,
        init: HTMLElement.()->Unit) {
    div { className = "${width.code}"
        init()
    }
}