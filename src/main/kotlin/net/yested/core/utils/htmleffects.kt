package net.yested.core.utils

import org.w3c.dom.HTMLElement

interface Effect {
    fun apply(htmlElement: HTMLElement, callback: Function0<Unit>? = null)
}

object NoEffect : Effect, BiDirectionEffect {
    override fun apply(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        callback?.invoke()
    }

    override fun applyIn(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        callback?.invoke()
    }

    override fun applyOut(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        callback?.invoke()
    }
}

/**
 * @param effectIn the effect to make the element come in to view (aka appear).
 * @param effectOut the effect to make the element go out of view (aka disappear).
 */
open class SimpleBiDirectionEffect(val effectIn: Effect, val effectOut: Effect): BiDirectionEffect {
    override fun applyIn(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        effectIn.apply(htmlElement, callback)
    }

    override fun applyOut(htmlElement: HTMLElement, callback: (() -> Unit)?) {
        effectOut.apply(htmlElement, callback)
    }
}

interface BiDirectionEffect {
    fun applyIn(htmlElement: HTMLElement, callback: Function0<Unit>? = null)
    fun applyOut(htmlElement: HTMLElement, callback: Function0<Unit>? = null)
}
