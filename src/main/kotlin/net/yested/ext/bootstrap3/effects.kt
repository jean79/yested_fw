package net.yested.ext.bootstrap3

import net.yested.core.utils.Effect
import net.yested.core.utils.SimpleBiDirectionEffect
import net.yested.ext.jquery.yestedJQuery
import org.w3c.dom.HTMLElement
import kotlin.browser.window
import kotlin.dom.hasClass

private val DURATION = 200
private val COLLAPSE_DURATION = DURATION * 2


class CollapseIn(private val duration: Int = COLLAPSE_DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        val jqElement = yestedJQuery(htmlElement)
        if (!htmlElement.hasClass("collapse")) {
            jqElement.addClass("collapse").children("td").children("*").addClass("collapse")
        }
        jqElement.collapse("show").children("td").children("*").collapse("show")
        window.setTimeout({ callback?.invoke() }, duration)
    }
}

class CollapseOut(private val duration: Int = COLLAPSE_DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        val jqElement = yestedJQuery(htmlElement)
        if (!htmlElement.hasClass("collapse")) {
            jqElement.addClass("collapse in").children("td").children("*").addClass("collapse in")
        }
        jqElement.collapse("hide").children("td").children("*").collapse("hide")
        window.setTimeout({ callback?.invoke() }, duration)
    }
}

class Collapse : SimpleBiDirectionEffect(CollapseIn(), CollapseOut())
