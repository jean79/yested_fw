package net.yested.ext.pickadate

import globals.JQuery

/**
 * uses library: http://amsul.ca/pickadate.js/
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 1/21/17
 * Time: 6:44 AM
 */
class DateContext {
    var select: Double? = null
}

@Suppress("unused") // Used by pickadate itself
class PickADateOptions(var format: String,
                       var selectMonths: Boolean = false,
                       var selectYears: Boolean = false,
                       var clear: String = "Clear",
                       var container: String? = null,
                       var onSet: (DateContext) -> Unit)

@JsModule("pickadate/lib/picker.date") @JsNonModule
private external val pickadate: Any = definedExternally

@JsModule("pickadate/lib/picker.date") @JsNonModule
private external interface PickADateJQuery {
    fun pickadate(options: PickADateOptions): JQuery
}

fun JQuery.pickadate(options: PickADateOptions) {
    @Suppress("UNUSED_VARIABLE") val requirePickadate = pickadate
    this.unsafeCast<PickADateJQuery>().pickadate(options)
}
