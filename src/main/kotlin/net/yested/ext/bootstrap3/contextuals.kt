package net.yested.ext.bootstrap3

import net.yested.core.html.p
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLParagraphElement

enum class TextContext(val code: String) {
    Muted("muted"),
    Primary("primary"),
    Success("success"),
    Info("info"),
    Warning("warning"),
    Danger("danger") }

enum class BackgroundContext(val code: String) {
    Primary("primary"),
    Success("success"),
    Info("info"),
    Warning("warning"),
    Danger("danger") }

fun HTMLElement.contextualText(text: TextContext, init: HTMLParagraphElement.()->Unit) {
    p { className = "text-${text.code}"
        this.init()
    }
}

fun HTMLElement.contextualBackground(text: BackgroundContext, init: HTMLParagraphElement.()->Unit) {
    p { className = "bg-${text.code}"
        this.init()
    }
}
