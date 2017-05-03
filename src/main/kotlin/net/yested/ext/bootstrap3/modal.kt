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

interface DialogControl {
    fun showDialog()

    fun hideDialog()

    fun destroyDialog()
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

@native fun JQuery.modal(command: String) { noImpl }

enum class DialogSize(val code: String) {
    Small("sm"),
    Default("default"),
    Large("lg")
}

fun openDialog(size: DialogSize = DialogSize.Default, init:DialogContext.(dialog: DialogControl)->Unit): DialogControl {
    val dialog = prepareDialog(size, init)
    dialog.showDialog()
    return dialog
}

/**
 * Prepares a dialog without showing it.
 * This is useful as a lazy value so that it can be shown and hidden multiple times without wasting resources.
 * Any callbacks should be tied to a button added to the dialog.
 * @return DialogControl to enable showing and hiding it.
 */
fun prepareDialog(size: DialogSize = DialogSize.Default, init:DialogContext.(dialog: DialogControl)->Unit): DialogControl {
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
                        span { appendText(Typography.times.toString()) }
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

    val dialogControl = object: DialogControl {
        override fun showDialog() {
            jq(dialogElement).modal("show")
        }

        override fun hideDialog() {
            jq(dialogElement).modal("hide")
        }

        override fun destroyDialog() {
            jq(dialogElement).modal("destroy")
        }
    }

    DialogContext(header = header!!, body = body!!, footer = footer!!).init(dialogControl)

    return dialogControl
}
