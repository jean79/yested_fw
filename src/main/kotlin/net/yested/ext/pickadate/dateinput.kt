package net.yested.ext.pickadate

import net.yested.core.html.bind
import net.yested.core.properties.Property
import net.yested.core.utils.whenAddedToDom
import net.yested.ext.jquery.yestedJQuery
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

/**
 * A dateInput.
 * @param clearLabel the label to put on the "Clear" button on the pick-a-date dialog.
 * @param containerSelector any valid CSS selector for the container to place the picker's root element.
 */
fun HTMLElement.dateInput(data: Property<Moment?>,
                          placeholder: String? = null,
                          clearLabel: String = "Clear",
                          containerSelector: String? = null,
                          formatter: FormatStringBuilder.()-> FormatString,
                          init: (HTMLInputElement.() -> Unit)? = null) {

    val formatString = FormatStringBuilder().formatter().toString()

    val text = data.asText(formatString)

    val element = document.createElement("input") as HTMLInputElement

    element.className = "date"
    element.size = formatString.length
    element.type = "text"
    if (placeholder != null) { element.placeholder = placeholder }
    element.bind(text)
    if (init != null) element.init()
    this.appendChild(element)

    val options = PickADateOptions(
            format= formatString.toLowerCase(),
            selectMonths = true,
            selectYears = true,
            clear = clearLabel,
            container = containerSelector,
            onSet = { context -> data.set(context.select?.let { Moment.fromMillisecondsSinceUnixEpoch(it) }) }
    )

    whenAddedToDom {
        text.onNext { setAttribute("data-value", it) }

        yestedJQuery(element).pickadate(options)
    }
}
