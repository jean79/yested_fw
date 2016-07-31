package net.yested.ext.bootstrap3

import net.yested.core.html.div
import net.yested.core.html.span
import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.utils.with
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.Event

enum class InputGroupSize(val code: String) {
    Large("input-group-lg"),
    Default(""),
    Small("input-group-lg")
}

class InputGroupContext(val el: HTMLDivElement) {

    fun addon(init:HTMLSpanElement.()->Unit) {
        el with {
            span { className = "input-group-addon"
                this.init()
            }
        }
    }

    fun input(init:HTMLDivElement.()->Unit) {
        el.init()
    }

    fun button(
            look: ButtonLook = ButtonLook.Default,
            onclick: ((Event)->Unit)? = null,
            active: Property<Boolean>? = null,
            disabled: ReadOnlyProperty<Boolean>? = null,
            init: HTMLButtonElement.()->Unit) {

        el with {
            span { className = "input-group-btn"
                btsButton(
                        look = look,
                        onclick = onclick,
                        active = active,
                        disabled = disabled,
                        init = init)
            }
        }

    }

    fun dropdown(label: String, init: DropDownContext.() -> Unit) {
        el with {
            div {
                className = "input-group-btn"
                generateDropdownInto(label, init)
            }
        }
    }

}

fun HTMLElement.inputGroup(size: InputGroupSize = InputGroupSize.Default, init: InputGroupContext.()->Unit) {
    var el: HTMLDivElement? = null
    div { className = "input-group ${size.code}"
       el = this
    }
    InputGroupContext(el!!).init()
}

