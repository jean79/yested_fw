package net.yested.ext.jquery

import globals.JQuery
import globals.JQueryStatic
import globals.jQuery
import org.w3c.dom.HTMLElement
import org.w3c.dom.Window

/**
 * JQuery functions that are are available via Yested to applications.
 * If you need additional functions, create similar code with a different Kotlin name.
 * Your new code can extend YestedJQuery, which will enable chaining into these functions.
 */
@Deprecated("use globals.jQuery", replaceWith = ReplaceWith("jQuery", "globals.jQuery"))
val yestedJQuery: JQuery = jQuery.unsafeCast<JQuery>()

@Deprecated("use jQuery(element)", replaceWith = ReplaceWith("jQuery(element)", "globals.jQuery"))
fun yestedJQuery(element: HTMLElement): JQuery = jQuery(element)

@Deprecated("use jQuery(window)", replaceWith = ReplaceWith("jQuery(window)", "globals.jQuery"))
fun yestedJQuery(window: Window): JQuery = jQuery(window)

@Deprecated("use JQuery")
typealias YestedJQuery = JQuery

fun JQuery.datetimepicker(param: Any?) {
    this.asDynamic().datetimepicker(param)
}

fun JQuery.modal(command: String) {
    this.asDynamic().modal(command)
}

fun <T> JQueryStatic.get(url:String, loaded:(response: T) -> Unit) {
    this.asDynamic().get(url, loaded)
}

fun <RESULT> JQueryStatic.ajax(request: AjaxRequest<RESULT>) {
    this.asDynamic().ajax(request)
}

@Deprecated("use JQuery", replaceWith = ReplaceWith("JQuery", "globals.JQuery"))
typealias JQueryWindow = JQuery
