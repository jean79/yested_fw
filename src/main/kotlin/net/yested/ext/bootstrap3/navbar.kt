package net.yested.ext.bootstrap3

import net.yested.core.html.setClassPresence
import net.yested.core.html.*
import net.yested.core.properties.Property
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.utils.with
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.dom.*

class NavbarMenuDropDown(val ul: HTMLUListElement) {

    fun item(active: Property<Boolean>? = null, init: HTMLLIElement.()->Unit) {
        ul with {
            li {
                active?.let { setClassPresence("active", it) }
                this.init()
            }
        }
    }

    fun separator() {
        ul with {
            li { className = "divider" }
        }
    }

    fun dropDownHeader(init: HTMLLIElement.()->Unit) {
        ul with {
            li { className = "dropdown-header"
                this.init()
            }
        }
    }

}

class NavbarMenu(val ul: HTMLUListElement) {

    fun item(active: Property<Boolean>? = null, init: HTMLLIElement.()->Unit) {
        ul with {
            li {
                active?.let { setClassPresence("active", it) }
                this.init()
            }
        }
    }

    fun dropDown(label: String, init: NavbarMenuDropDown.()->Unit) {

        var el:HTMLUListElement? = null
        ul with {
            li {
                a {
                    href = "#"; className = "dropdown-toggle"
                    setAttribute("data-toggle", "dropdown")
                    appendText(label)
                    span { className = "caret" }
                }
                el = ul {
                    className = "dropdown-menu"
                }
            }
        }
        NavbarMenuDropDown(el!!).init()
    }

}

enum class NavbarPosition(val code: String) {
    Left("navbar-left"),
    Right("navbar-right")
}

class NavbarContext(
        val navbar: HTMLDivElement,
        val navbarContainer: HTMLDivElement,
        val navbarHeader: HTMLElement,
        val contentElement: HTMLDivElement) {
    private var navbarToggle: HTMLElement? = null

    fun toggle(init: HTMLElement.()->Unit = { glyphicon("menu-hamburger") }) {
        // pull-left will be removed if a brand is added.
        button {
            className = "navbar-toggle collapsed pull-left"; type = "button"
            setAttribute("data-toggle", "collapse")
            setAttribute("data-target", "#navbar")
            setAttribute("aria-expanded", "false")
            setAttribute("aria-controls", "navbar")
            span { className = "sr-only"; appendText("Toggle Navigation") }
            navbarToggle = this
            init()
        }
    }

    fun brand(init:HTMLElement.()->Unit) {
        navbarToggle?.removeClass("pull-left")
        navbarHeader.a { className = "navbar-brand"; href = "#"
            init ()
        }
    }

    fun menu(
            position: NavbarPosition = NavbarPosition.Left,
            init:NavbarMenu.()->Unit) {
        contentElement.ul { className = "nav navbar-nav ${position.code}"
            NavbarMenu(ul = this).init()
        }
    }

    fun form(
            position: NavbarPosition = NavbarPosition.Left,
            init: HTMLFormElement.()->Unit) {
        contentElement.form { className = "navbar-form ${position.code}"
            init()
        }
    }

    fun button(
            position: NavbarPosition = NavbarPosition.Left,
            look: ButtonLook = ButtonLook.Default,
            onclick: ((Event)->Unit)? = null,
            active: Property<Boolean>? = null,
            disabled: ReadOnlyProperty<Boolean>? = null,
            init: HTMLButtonElement.()->Unit) {
        contentElement.btsButton(look = look, onclick = onclick, active = active, disabled = disabled) {
            addClass("navbar-btn", position.code)
            init()
        }
    }

    fun text(
            position: NavbarPosition = NavbarPosition.Left,
            init: HTMLParagraphElement.()->Unit) {
        contentElement.p { className = "navbar-text ${position.code}"
            init()
        }
    }

}

enum class NavbarCompletePosition(val code: String) {
    Top(code = ""),
    FixedTop(code = "navbar-fixed-top"),
    FixedBottom(code = "navbar-fixed-bottom"),
    StaticTop(code = "navbar-static-top")
}

fun HTMLElement.navbar(
        position: NavbarCompletePosition = NavbarCompletePosition.Top,
        inverted: Boolean = false,
        containerWidth: ContainerWidth = ContainerWidth.Fixed,
        init: NavbarContext.()->Unit) {

    var navbar: HTMLDivElement? = null
    var navbarContainer: HTMLDivElement? = null
    var navbarHeader: HTMLDivElement? = null
    var contentElement: HTMLDivElement? = null

    nav {  className = "navbar ${if (inverted) "navbar-inverse" else if (position == NavbarCompletePosition.Top) "" else "navbar-default"} ${position.code}"
        navbar = this
        container(width = containerWidth) {
            div { className = "navbar-header"
                navbarHeader = this
            }
            div { id = "navbar"; className = "navbar-collapse collapse"
                setAttribute("aria-expanded", "false")
                contentElement = this
            }
            navbarContainer = this
        }
    }

    NavbarContext(navbar = navbar!!, navbarContainer = navbarContainer!!, navbarHeader = navbarHeader!!,  contentElement = contentElement!!).init()

}