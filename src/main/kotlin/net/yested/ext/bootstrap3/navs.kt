package net.yested.ext.bootstrap3

import net.yested.core.html.li
import net.yested.core.html.ul
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.properties.toProperty
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLUListElement
import kotlin.dom.addClass
import kotlin.dom.removeClass

class NavContext(val el: HTMLUListElement) {

    fun item(
            active: ReadOnlyProperty<Boolean> = true.toProperty(),
            disabled: ReadOnlyProperty<Boolean> = false.toProperty(),
            init: HTMLLIElement.()->Unit) {
        el.li {
            active.onNext {
                if (it) {
                    addClass("active")
                } else {
                    removeClass("active")
                }
            }
            disabled.onNext {
                if (it) {
                    addClass("disabled")
                } else {
                    removeClass("disabled")
                }
            }
            init()
        }
    }
    //TODO: add dropdown http://getbootstrap.com/components/#nav-dropdowns
}

enum class TabsFormat(val code: String) {
    Tabs("nav-tabs"),
    Pills("nav-pills"),
    PillsStacked("nav-pills nav-stacked")
}

fun HTMLElement.navTabs(format: TabsFormat = TabsFormat.Tabs, justified: Boolean = false, init: NavContext.()->Unit) {
    val el = ul { className = "nav ${format.code} ${if (justified) "nav-justified" else ""}" }
    NavContext(el).init()
}