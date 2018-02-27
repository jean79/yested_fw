package net.yested.ext.jquery

import org.w3c.dom.HTMLElement
import org.w3c.dom.Window

/**
 * JQuery functions that are are available via Yested to applications.
 * If you need additional functions, create similar code with a different Kotlin name.
 * Your new code can extend YestedJQuery, which will enable chaining into these functions.
 */
@JsModule("jquery") @JsNonModule @JsName("$") external val yestedJQuery: YestedJQuery
@JsModule("jquery") @JsNonModule @JsName("$") external fun yestedJQuery(element: HTMLElement): YestedJQuery = definedExternally
@JsModule("jquery") @JsNonModule @JsName("$") external fun yestedJQuery(window: Window): JQueryWindow

external interface YestedJQuery {
    fun datetimepicker(param: Any? ): Unit
    fun fadeOut(duration: Int, callback:()->Unit): YestedJQuery
    fun fadeIn(duration: Int, callback:()->Unit): YestedJQuery
    fun slideUp(duration: Int = definedExternally, callback:(()->Unit)? = definedExternally): YestedJQuery
    fun slideDown(duration: Int = definedExternally, callback:(()->Unit)? = definedExternally): YestedJQuery
    fun show(callback:()->Unit): YestedJQuery
    fun hide(callback:()->Unit): YestedJQuery
    fun addClass(classNames: String): YestedJQuery
    fun attr(name: String, value: String): YestedJQuery
    fun children(selector: String): YestedJQuery
    fun modal(command: String)
    fun on(event: String, handler: ()->Unit): Unit
    fun <T> get(url:String, loaded:(response: T) -> Unit) : Unit
    //fun post(url:String, data:Any?, handler:()->Unit, type:String = "json") : Unit = definedExternally
    //fun ajax(url:String, type:String, contentType:String, dataType:String, data:Any, success:()->Unit) : Unit = definedExternally
    fun <RESULT> ajax(request: AjaxRequest<RESULT>) : Unit
}

external interface JQueryWindow {
    fun on(eventName:String, listener:() -> Unit):Unit
    fun trigger(eventName:String):Unit
}
