package globals

import kotlin.browser.*

@JsModule("jquery") @JsNonModule @JsName("$") private external val jQueryFromModule: JQueryStatic

val jQuery: JQueryStatic = publishGlobalJQuery()

private fun publishGlobalJQuery(): JQueryStatic {
    if (window.asDynamic().`$` == null) {
        window.asDynamic().`$` = jQueryFromModule
    }
    if (window.asDynamic().jQuery == null) {
        window.asDynamic().jQuery = jQueryFromModule
    }
    return jQueryFromModule
}

fun main() {
    publishGlobalJQuery()
}
