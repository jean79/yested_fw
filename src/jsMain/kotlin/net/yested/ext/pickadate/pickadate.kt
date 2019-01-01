package net.yested.ext.pickadate

import net.yested.ext.jquery.YestedJQuery

/**
 * uses library: http://amsul.ca/pickadate.js/
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 1/21/17
 * Time: 6:44 AM
 */
class DateContext {
    var select: Double? = null
}

@JsModule("pickadate/lib/picker.date") @JsNonModule @JsName("$") external val requirePickadate: Any = definedExternally

class PickADateOptions(var format: String,
                       var selectMonths: Boolean = false,
                       var selectYears: Boolean = false,
                       var clear: String = "Clear",
                       var container: String? = null,
                       var onSet: (DateContext) -> Unit) {
    companion object {
        init { console.info(requirePickadate) }
    }
}

@JsModule("pickadate/lib/picker.date") @JsNonModule external interface PickADateJQuery {
    fun pickadate(options: PickADateOptions): YestedJQuery
}

fun YestedJQuery.pickadate(options: PickADateOptions) {
    @Suppress("UNCHECKED_CAST_TO_NATIVE_INTERFACE")
    (this as PickADateJQuery).pickadate(options)
}
