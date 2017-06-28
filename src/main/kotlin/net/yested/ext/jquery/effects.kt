package net.yested.ext.jquery

import net.yested.core.utils.BiDirectionEffect
import net.yested.core.utils.Effect
import net.yested.core.utils.SimpleBiDirectionEffect
import net.yested.core.utils.setChild
import org.w3c.dom.HTMLElement
import kotlin.browser.window

private val DURATION = 200
private val SLIDE_DURATION = DURATION * 2

class Show() : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        yestedJQuery(htmlElement).show { callback?.invoke() }
    }
}

class Hide() : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        yestedJQuery(htmlElement).hide { callback?.invoke() }
    }
}

class SlideUp(private val duration: Int = SLIDE_DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: Function0<Unit>?) {
        yestedJQuery(htmlElement).slideUp(duration) { callback?.invoke() }
    }
}

class SlideDown(private val duration: Int = SLIDE_DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        yestedJQuery(htmlElement).slideDown(duration) { callback?.invoke() }
    }
}

class FadeOut(private val duration: Int = DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        yestedJQuery(htmlElement).fadeOut(duration) { callback?.invoke() }
    }
}

class FadeIn(private val duration: Int = DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        yestedJQuery(htmlElement).fadeIn(duration) { callback?.invoke() }
    }
}

class Fade(duration: Int = DURATION) : SimpleBiDirectionEffect(FadeIn(duration), FadeOut(duration))

class Slide(duration: Int = SLIDE_DURATION) : SimpleBiDirectionEffect(SlideDown(duration), SlideUp(duration))

class SlideDownTableRow(private val duration: Int = SLIDE_DURATION) : Effect {
    private val startingPointEffect = SlideUpTableRow(duration = 0)

    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        // start it out hidden in a way that slideDown will show it.
        startingPointEffect.apply(htmlElement) {
            // now animate showing it
            val jq = yestedJQuery(htmlElement)
            val jqTdElements = jq.children("td")
            jqTdElements.slideDown(duration).children("*").slideDown(duration)
            window.setTimeout({
                jqTdElements.attr("style", "").children("*").attr("style", "")
                callback?.invoke()
            }, duration)
        }
    }
}

class SlideUpTableRow(private val duration: Int = SLIDE_DURATION) : Effect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        yestedJQuery(htmlElement).children("td").slideUp(duration).children("*").slideUp(duration)
        if (callback != null) {
            window.setTimeout(callback, duration)
        }
    }
}

class SlideTableRow(duration: Int = SLIDE_DURATION) : SimpleBiDirectionEffect(
        SlideDownTableRow(duration), SlideUpTableRow(duration))

fun HTMLElement.setChild(child: HTMLElement, effect: BiDirectionEffect, callback: Function0<Unit>? = null) {
    effect.applyOut(this) {
        setChild(child)
        effect.applyIn(this) {
            callback?.invoke()
        }
    }
}
