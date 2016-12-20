package net.yested.ext.bootstrap3

import jquery.JQuery
import jquery.jq
import net.yested.core.html.div
import net.yested.core.html.span
import net.yested.core.properties.Property
import net.yested.core.properties.mapBidirectionally
import net.yested.core.utils.whenAddedToDom
import net.yested.ext.bootstrap3.utils.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement


//TODO: support Locales: http://momentjs.com/docs/#/i18n/

fun Property<Moment?>.asText(formatString: String): Property<String> {
    return this.mapBidirectionally(
                transform = { if (it == null) "" else it.format(formatString) },
                reverse = { if (it.isEmpty()) null else Moment.parse(it, formatString) })
}

/**
 *
 * uses library: https://github.com/Eonasdan/bootstrap-datetimepicker/blob/master/build/js/bootstrap-datetimepicker.min.js
 */
fun HTMLElement.dateInput(data: Property<Moment?>, placeholder: String? = null, formatter: FormatStringBuilder.()-> FormatString) {
    val formatString = FormatStringBuilder().formatter().toString()

    val text = data.asText(formatString)
    var inputElement: HTMLInputElement? = null

    val element = div { className = "input-group date"
        textInput(text) { size = formatString.length
            inputElement = this
            if (placeholder != null) { this.placeholder = placeholder }
        }
        span {
            className = "input-group-addon"
            span { className = "glyphicon glyphicon-calendar" } // TODO: style = "cursor: pointer;"
        }
    }

    element.whenAddedToDom {
        val param = object {
            val format = formatString
        }
        // Hack: datetimepicker cannot handle unknown parameters, and Kotlin add a $metadata$ property to every object
        js("delete param.\$metadata$")
        jq(element).datetimepicker(param)

        jq(element).on("dp.change", {
            text.set(inputElement!!.value)
        })
    }
}

private @native fun JQuery.datetimepicker(param: Any? ): Unit = noImpl;
private @native fun JQuery.on(event: String, handler: (dynamic)->Unit): Unit = noImpl;
