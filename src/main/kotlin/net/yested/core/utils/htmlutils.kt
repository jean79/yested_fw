package net.yested.core.utils

import org.w3c.dom.Node

fun Node.removeAllChildElements() {
    while (this.firstChild != null) {
        this.removeChild(this.firstChild!!)
    }
}