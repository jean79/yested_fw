package net.yested.ext.bootstrap3

import net.yested.core.html.div
import net.yested.core.html.form
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement

fun HTMLElement.formGroup(init: HTMLDivElement.()->Unit) {
    div { className = "form-group"
        init()
    }
}

enum class FormFormat(val code: String) {
    Default(""),
    Horizontal("form-horizontal"),
    Inline("form-inline")
}
fun HTMLElement.btsForm(format: FormFormat = FormFormat.Default, init: HTMLFormElement.()->Unit) {
    form { className = format.code
        init()
    }
}