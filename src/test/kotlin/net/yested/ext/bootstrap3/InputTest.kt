package net.yested.ext.bootstrap3

import net.yested.core.properties.Property
import net.yested.core.properties.toProperty
import net.yested.ext.bootstrap3.utils.*
import org.junit.Test
import spec.*

/**
 * A test for [dateInput], etc.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 9/29/16
 * Time: 1:51 PM
 */
class InputTest {
    @Test
    fun MomentProperty_asText_shouldKeepInSync() {
        val builder = FormatStringBuilder()
        val formatter = builder.year.fourDigits + "." + builder.month.twoDigits + "." + builder.dayOfMonth.twoDigits

        val moment: Property<Moment?> = Moment.now().toProperty()
        val thisYear = moment.get()!!.year
        val text: Property<String> = moment.asText(formatter.toString())
        text.get().mustContain(thisYear.toString())

        text.set("2015.12.21")
        moment.get()?.year.mustBe(2015)

        moment.set(Moment.now())
        text.get().mustContain(thisYear.toString())
    }

    @Test
    fun MomentProperty_asText_shouldUseFormatString() {
        val builder = FormatStringBuilder()

        val inputFormatString = (builder.month.oneDigit + "/" + builder.dayOfMonth.oneDigit + "/" + builder.year.fourDigits).toString()
        val moment: Property<Moment?> = Moment.parse("1/30/2015", inputFormatString).toProperty()

        val formatter = builder.month.oneDigit + " @ " + builder.dayOfMonth.oneDigit + "!" + builder.year.fourDigits
        val text: Property<String> = moment.asText(formatter.toString())
        text.get().mustBe("1 @ 30!2015")
    }
}
