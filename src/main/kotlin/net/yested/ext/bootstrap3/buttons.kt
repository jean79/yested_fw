package net.yested.ext.bootstrap3

import net.yested.core.html.*
import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.utils.with
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.dom.addClass
import kotlin.dom.plus
import kotlin.dom.removeClass

enum class ButtonLook(val code: String) {
    Default("default"),
    Primary("primary"),
    Success("success"),
    Info("info"),
    Warning("warning"),
    Danger("danger"),
    Link("link")
}

enum class ButtonSize(val code: String) {
    Large("btn-lg"),
    Default("btn-default"),
    Small("btn-sm"),
    ExtraSmall("btn-xs")
}

fun HTMLElement.btsButton(
        look: ButtonLook = ButtonLook.Default,
        size: ButtonSize = ButtonSize.Default,
        block: Boolean = false,
        onclick: ((Event)->Unit)? = null,
        active: Property<Boolean>? = null,
        disabled: ReadOnlyProperty<Boolean>? = null,
        init: HTMLButtonElement.()->Unit) {
    button { className = "btn btn-${look.code} ${size.code} ${if (block) "btn-block" else ""}"; type = "submit"
        addEventListener("click", {  event ->
            onclick?.let { onclick(event) }
            active?.set(!active.get())
        }, false)
        init()
        active?.onNext {
            if (it) {
                addClass("active")
            } else {
                removeClass("active")
            }
        }
        disabled?.onNext {
            this.disabled = it
        }
    }
}

class DropDownContext(val ul: HTMLUListElement) {

    fun item(active: Property<Boolean>? = null, init: HTMLLIElement.()->Unit) {
        ul with {
            li {
                active?.onNext { if (it) addClass("active") else removeClass("active") }
                this.init()
            }
        }
    }

    fun separator() {
        ul with {
            li { className = "divider" }
        }
    }

}

enum class Orientation(val code: String) {
    Up("dropup"),
    Down("dropdown")
}

fun HTMLDivElement.generateDropdownInto(label: String, init: DropDownContext.() -> Unit) {
    button {
        className = "btn btn-default dropdown-toggle"; type = "button"
        setAttribute("data-toggle", "dropdown")
        plus(label)
        plus(" ")
        span { className = "caret" }
    }
    val el = ul { className = "dropdown-menu" }
    DropDownContext(el).init()
}

fun HTMLElement.dropdown(
        label: String,
        orientation: Orientation = Orientation.Down,
        init: DropDownContext.()->Unit) {

    div { className = orientation.code
        generateDropdownInto(label, init)
    }

}

enum class ButtonGroupSize(val code: String) {
    Large("btn-group-lg"),
    Default("btn-group-default"),
    Small("btn-group-sm"),
    ExtraSmall("btn-group-xs")
}

class ButtonGroupContext(val context: HTMLElement) {

    fun button(
            look: ButtonLook = ButtonLook.Default,
            size: ButtonSize = ButtonSize.Default,
            onclick: ((Event)->Unit)? = null,
            active: Property<Boolean>? = null,
            disabled: ReadOnlyProperty<Boolean>? = null,
            init: HTMLButtonElement.()->Unit) {

        context.btsButton(
                look = look,
                size = size,
                onclick = onclick,
                active = active,
                disabled = disabled,
                init = init)
    }

    fun dropdown(
            label: String,
            init: DropDownContext.()->Unit) {

        context with {
            div {
                className = "btn-group"
                generateDropdownInto(label, init)
            }
        }

    }

}

fun HTMLElement.buttonGroup(size: ButtonGroupSize = ButtonGroupSize.Default, init: ButtonGroupContext.()->Unit) {
    val el = div { className = "btn-group ${size.code}" }
    ButtonGroupContext(el).init()
}

class ButtonToolbarContext(val size: ButtonGroupSize = ButtonGroupSize.Default, val context: HTMLElement) {
    fun group(init:ButtonGroupContext.()->Unit) {
        context.buttonGroup(size = size, init = init)
    }
}

fun HTMLElement.buttonToolbar(size: ButtonGroupSize = ButtonGroupSize.Default, init: ButtonToolbarContext.()->Unit) {
    val el = div { className = "btn-toolbar" }
    ButtonToolbarContext(size = size, context = el).init()
}