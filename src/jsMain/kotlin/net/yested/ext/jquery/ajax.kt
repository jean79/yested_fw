package net.yested.ext.jquery

data class AjaxRequest<RESULT>(val url:String, val type:String = "POST", val data:String,
                       val contentType:String = "application/json; charset=utf-8",
                       val dataType:String = "json", val success: ((RESULT) -> Unit))

 fun <T> ajaxGet(url:String, loaded:(response:T) -> Unit) : Unit {
    yestedJQuery.get(url = url, loaded = loaded)
}

 fun <RESULT> ajaxPost(ajaxRequest: AjaxRequest<RESULT>) : Unit {
    yestedJQuery.ajax(ajaxRequest)
}
