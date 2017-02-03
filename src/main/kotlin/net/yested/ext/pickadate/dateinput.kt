package net.yested.ext.pickadate

import jquery.jq
import net.yested.core.html.bind
import net.yested.core.properties.Property
import net.yested.core.utils.whenAddedToDom
import net.yested.ext.moment.FormatString
import net.yested.ext.moment.FormatStringBuilder
import net.yested.ext.moment.Moment
import net.yested.ext.moment.asText
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document

/**
 * uses library: http://amsul.ca/pickadate.js/
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 1/21/17
 * Time: 6:44 AM
 */

/** A dateInput. */
fun HTMLElement.dateInput(data: Property<Moment?>, placeholder: String? = null, formatter: FormatStringBuilder.()-> FormatString) {
    val formatString = FormatStringBuilder().formatter().toString()

    val text = data.asText(formatString)

    val element = document.createElement("input") as HTMLInputElement

    element.className = "date"
    element.size = formatString.length
    element.type = "text"
    if (placeholder != null) { element.placeholder = placeholder }
    element.bind(text)
    this.appendChild(element)
    whenAddedToDom {
        text.onNext { setAttribute("data-value", it) }

        jq(this).pickadate(PickADateOptions(formatString.toLowerCase(), selectMonths = true, selectYears = true,
                onSet = { context: DateContext ->
                    if (context.select != undefined)
                        data.set(context.select?.let { Moment.parseMillisecondsSinceUnixEpoch(it) })
                })
        )
    }
}
