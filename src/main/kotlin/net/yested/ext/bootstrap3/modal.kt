package net.yested.ext.bootstrap3

import jquery.JQuery
import jquery.jq
import net.yested.core.html.button
import net.yested.core.html.div
import net.yested.core.html.h4
import net.yested.core.html.span
import net.yested.core.utils.Div
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement
import kotlin.dom.appendText

interface DialogInterface {
    fun closeDialog()
}

class DialogContext internal constructor(val header: HTMLHeadingElement, val body: HTMLDivElement, val footer: HTMLDivElement) {

    fun header(init:HTMLHeadingElement.()->Unit) {
        header.init()
    }

    fun body(init:HTMLDivElement.()->Unit) {
        body.init()
    }

    fun footer(init:HTMLDivElement.()->Unit) {
        footer.init()
    }

}

@native fun JQuery.modal(command: String): Nothing = noImpl

enum class DialogSize(val code: String) {
    Small("sm"),
    Default("default"),
    Large("lg")
}

fun openDialog(size: DialogSize = DialogSize.Default, init:DialogContext.(dialog: DialogInterface)->Unit) {

    var header: HTMLHeadingElement? = null
    var body: HTMLDivElement? = null
    var footer: HTMLDivElement? = null

    val dialogElement = Div {
        className = "modal fade"
        div { className = "modal-dialog modal-${size.code}"
            div {
                className = "modal-content"
                div {
                    className = "modal-header"
                    button {
                        type = "button"; className = "close"; setAttribute("data-dismiss", "modal")
                        span { appendText("&times;") }
                    }
                    h4 {
                        className = "modal-title"
                        header = this
                    }

                }
                div {
                    className = "modal-body"
                    body = this
                }
                div {
                    className = "modal-footer"
                    footer = this
                }
            }
        }
    }

    val dialog = object: DialogInterface {
        override fun closeDialog() {
            jq(dialogElement).modal("hide")
        }
    }

    DialogContext(header = header!!, body = body!!, footer = footer!!).init(dialog)

    jq(dialogElement).modal("show")

}