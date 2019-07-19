package net.yested.ext.bootstrap3

import globals.JQuery
import globals.jQuery
import net.yested.core.utils.Effect
import net.yested.core.utils.SimpleBiDirectionEffect
import net.yested.core.html.*
import org.w3c.dom.HTMLElement
import kotlin.browser.window

private const val DURATION = 200
private const val COLLAPSE_DURATION = DURATION * 2

@Suppress("unused")
private val requireGlobalJQuery = jQuery

@JsModule("bootstrap") @JsNonModule @JsName("$") external val bootstrap: Any = definedExternally

@Suppress("unused")
private val requireBootstrap = bootstrap

fun JQuery.collapse(action: String? = null): JQuery {
    return asDynamic().collapse(action).unsafeCast<JQuery>()
}

class CollapseIn(private val duration: Int = COLLAPSE_DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        val jqElement = jQuery(htmlElement)
        if (!htmlElement.hasClass2("collapse")) {
            jqElement.addClass("collapse").children("td").children("*").addClass("collapse")
        }
        jqElement.collapse("show").children("td").children("*").collapse("show")
        window.setTimeout({ callback?.invoke() }, duration)
    }
}

class CollapseOut(private val duration: Int = COLLAPSE_DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        val jqElement = jQuery(htmlElement)
        if (!htmlElement.hasClass2("collapse")) {
            jqElement.addClass("collapse in").children("td").children("*").addClass("collapse in")
        }
        jqElement.collapse("hide").children("td").children("*").collapse("hide")
        window.setTimeout({ callback?.invoke() }, duration)
    }
}

class Collapse : SimpleBiDirectionEffect(CollapseIn(), CollapseOut())
