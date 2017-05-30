package net.yested.ext.jquery

import jquery.JQuery
import jquery.jq
import net.yested.core.utils.setChild
import org.w3c.dom.HTMLElement
import kotlin.browser.window

@native fun JQuery.fadeOut(duration: Int, callback:()->Unit): JQuery = noImpl;
@native fun JQuery.fadeIn(duration: Int, callback:()->Unit): JQuery = noImpl;
@native fun JQuery.slideUp(duration: Int = 400, callback:(()->Unit)? = null): JQuery = noImpl
@native fun JQuery.slideDown(duration: Int = 400, callback:(()->Unit)? = null): JQuery = noImpl
@native fun JQuery.show(callback:()->Unit): JQuery = noImpl;
@native fun JQuery.hide(callback:()->Unit): JQuery = noImpl;
@native fun JQuery.children(selector: String): JQuery = noImpl

fun JQuery.slideUpTableRow(duration:Int = 400, callback:(()->Unit)? = null): JQuery {
    children("td").slideUp(duration).children("*").slideUp(duration)
    if (callback != null) {
        window.setTimeout({
            callback()
        }, duration)
    }
    return this
}

fun JQuery.slideDownTableRow(duration:Int = 400, callback:(()->Unit)? = null): JQuery {
    children("td").slideDown(duration).children("*").slideDown(duration)
    if (callback != null) {
        window.setTimeout({
            callback()
        }, duration)
    }
    return this
}

private val DURATION = 200
private val SLIDE_DURATION = DURATION * 2

interface Effect {
    fun apply(htmlElement: HTMLElement, callback: Function0<Unit>? = null)
}

interface BiDirectionEffect {
    fun applyIn(htmlElement: HTMLElement, callback: Function0<Unit>? = null)
    fun applyOut(htmlElement: HTMLElement, callback: Function0<Unit>? = null)
}

private fun call(function: Function0<Unit>?) {
    function?.let { function() }
}

 class Show() : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        jq(htmlElement).show { call(callback) }
    }
}

 class Hide() : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        jq(htmlElement).hide { call(callback) }
    }
}

 class SlideUp() : Effect {
    override fun apply(htmlElement: HTMLElement, callback: Function0<Unit>?) {
        jq(htmlElement).slideUp(SLIDE_DURATION) { call(callback) }
    }
}

 class SlideDown : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        jq(htmlElement).slideDown(SLIDE_DURATION) { call(callback) }
    }
}

 class FadeOut : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        jq(htmlElement).fadeOut(DURATION) { call(callback) }
    }
}

 class FadeIn : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        jq(htmlElement).fadeIn(DURATION) { call(callback) }
    }
}

 class Fade : BiDirectionEffect {
    override fun applyIn(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        FadeIn().apply(htmlElement, callback)
    }

    override fun applyOut(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        FadeOut().apply(htmlElement, callback)
    }
}

 class Slide : BiDirectionEffect {
    override fun applyIn(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        SlideDown().apply(htmlElement, callback)
    }

    override fun applyOut(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        SlideUp().apply(htmlElement, callback)
    }
}

fun HTMLElement.setChild(child: HTMLElement, effect: BiDirectionEffect, callback: Function0<Unit>? = null) {
    effect.applyOut(this) {
        setChild(child)
        effect.applyIn(this) {
            callback?.invoke()
        }
    }
}
