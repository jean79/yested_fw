package net.yested.ext.bootstrap3

import net.yested.core.html.button
import net.yested.core.html.div
import net.yested.core.html.h4
import net.yested.core.html.span
import net.yested.core.utils.Div
import net.yested.ext.jquery.yestedJQuery
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement
import kotlin.dom.addClass
import kotlin.dom.appendText
import kotlin.dom.removeClass

interface DialogControl {
    fun showDialog()

    fun hideDialog()

    fun destroyDialog()
}

class DialogContext internal constructor(val header: HTMLHeadingElement, val body: HTMLDivElement, val footer: HTMLDivElement) {

    var focusElement: org.w3c.dom.HTMLElement? = null

    fun header(init:HTMLHeadingElement.()->Unit) {
        header.init()
    }

    fun body(init:HTMLDivElement.()->Unit) {
        body.removeClass("hidden")
        body.init()
    }

    fun footer(init:HTMLDivElement.()->Unit) {
        footer.removeClass("hidden")
        footer.init()
    }

}

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
                    addClass("modal-body hidden") // DialogContext.body will unhide this
                    body = this
                }
                div {
                    addClass("modal-footer hidden") // DialogContext.footer will unhide this
                    footer = this
                }
            }
        }
    }

    val dialogContext = DialogContext(header = header!!, body = body!!, footer = footer!!)

    val dialogControl = object: DialogControl {
        override fun showDialog() {
            val yestedJQuery = yestedJQuery(dialogElement)
            yestedJQuery.modal("show")
            yestedJQuery.on("shown.bs.modal") {
                dialogContext.focusElement?.focus()
            }
        }

        override fun hideDialog() {
            yestedJQuery(dialogElement).modal("hide")
        }

        override fun destroyDialog() {
            yestedJQuery(dialogElement).modal("destroy")
        }
    }

    dialogContext.init(dialogControl)

    return dialogControl
}
