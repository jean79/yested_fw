package net.yested.ext.jquery

import org.w3c.xhr.XMLHttpRequest

data class AjaxRequest<RESULT>(val url: String,
                               val type: String = "POST",
                               val data: String = "",
                               val contentType: String = "application/json; charset=utf-8",
                               val dataType: String = "json",
                               val success: ((RESULT) -> Unit),
                               val error: ((jqXhr: XMLHttpRequest, textStatus: String, errorThrown: String) -> Unit))

@Deprecated("obsolete", replaceWith = ReplaceWith("yestedJQuery.get(url, loaded)"))
fun <T> ajaxGet(url:String, loaded:(response:T) -> Unit) {
    yestedJQuery.get(url = url, loaded = loaded)
}

@Deprecated("obsolete", replaceWith = ReplaceWith("yestedJQuery.ajax(ajaxRequest)"))
fun <RESULT> ajaxPost(ajaxRequest: AjaxRequest<RESULT>) {
    yestedJQuery.ajax(ajaxRequest)
}
