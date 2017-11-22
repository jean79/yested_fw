package net.yested.core.html

import QUnit.Assert
import net.yested.core.properties.*
import net.yested.core.utils.Div
import net.yested.core.utils.NoEffect
import net.yested.ext.bootstrap3.Collapse
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTableSectionElement
import org.w3c.dom.HTMLUListElement
import spec.*
import kotlin.browser.window
import kotlin.dom.appendText
import kotlin.test.Test

/**
 * A test for [tbody] and [repeatLive].
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 9/29/16
 * Time: 1:51 PM
 */
class HtmlBindTest {
    data class ListAssert(val list: List<Int>, val expectedText: String, val expectedIds: String)

    @Test
    fun repeatLiveShouldReflectData(assert: Assert) {
        val data: Property<List<Int>?> = listOf(1).toProperty()
        var ul: HTMLUListElement? = null
        var nextId = 1
        Div {
            ul = ul {
                repeatLive(data, NoEffect) { item ->
                    li { id = (nextId++).toString()
                        appendText(item.toString())
                    }
                }
                li { appendText("Last") }
            }
        }
        validateDataChanges(assert, ul!!, data, suffix = "Last", animate = false)
    }

    @Test
    fun tableShouldReflectData(assert: Assert) {
        tableShouldReflectData(assert, animate = false)
    }

    @Test
    fun tableShouldReflectData_animated(assert: Assert) {
        tableShouldReflectData(assert, animate = true)
    }

    private fun tableShouldReflectData(assert: Assert, animate: Boolean) {
        val data: Property<List<Int>?> = listOf(1).toProperty()
        var tbody: HTMLTableSectionElement? = null
        var nextId = 1
        Div {
            table {
                thead {
                    th {
                        appendText("Items")
                    }
                }
                tbody = tbody(data, if (animate) Collapse() else NoEffect) { item ->
                    tr { id = (nextId++).toString()
                        td { appendText(item.toString()) }
                    }
                }
            }
        }
        validateDataChanges(assert, tbody!!, data, animate = animate)
    }

    private fun validateDataChanges(assert: Assert, containerElement: HTMLElement, data: Property<List<Int>?>, suffix: String = "", animate: Boolean) {
        val done = assert.async()
        containerElement.textContent.mustBe("1$suffix")

        val listAssertSequence = listOf(
                ListAssert(listOf(1, 2), "12$suffix", "1,2"),
                ListAssert(listOf(1, 2, 3), "123$suffix", "1,2,3"),
                ListAssert(listOf(2, 3, 1), "231$suffix", "2,3,4"),
                ListAssert(listOf(1, 2, 3), "123$suffix", "5,2,3"),
                ListAssert(listOf(1, 3, 2), "132$suffix", "5,6,2"),
                ListAssert(listOf(1, 2, 3, 4, 5, 6, 7, 8), "12345678$suffix", "5,7,6,8,9,10,11,12"),
                ListAssert(listOf(1, 2, 3, 8, 7, 4, 5, 6), "12387456$suffix", "5,7,6,13,14,8,9,10"),
                ListAssert(listOf(1, 2, 3, 4, 5, 6, 7, 8), "12345678$suffix", "5,7,6,8,9,10,16,15"),
                ListAssert(listOf(1, 2, 3, 6, 7, 8, 4, 5), "12367845$suffix", "5,7,6,10,16,15,18,17"),
                ListAssert(listOf(1, 2, 3, 6, 8, 4, 5), "1236845$suffix", "5,7,6,10,15,18,17"),
                ListAssert(listOf(1, 2, 3, 4, 5), "12345$suffix", "5,7,6,18,17"),
                ListAssert(listOf(5, 4, 3, 2, 1), "54321$suffix", "19,20,21,22,5"),
                ListAssert(listOf(10, 11, 12, 13), "10111213$suffix", "23,24,25,26"),
                ListAssert(listOf(12, 13, 14), "121314$suffix", "25,26,27"))

        val listAssertIterator = listAssertSequence.iterator()
        validateListAsserts(listAssertIterator, containerElement, data, done, if (animate) 500 else 0)
    }

    private fun validateListAsserts(listAssertIterator: Iterator<ListAssert>, containerElement: HTMLElement?, data: Property<List<Int>?>, done: () -> Unit, stepDelay: Int) {
        if (listAssertIterator.hasNext()) {
            val listAssert = listAssertIterator.next()
            // This callback will be called once tbody animation rendering is done
            if (data.get() != listAssert.list) {
                data.set(listAssert.list)
                window.setTimeout({
                    containerElement!!.textContent.mustBe(listAssert.expectedText)
                    getChildIdsAsString(containerElement).mustBe(listAssert.expectedIds)
                    containerElement.styleContent.mustBe("")
                    validateListAsserts(listAssertIterator, containerElement, data, done, stepDelay)
                }, stepDelay)
            } else {
                console.info("skipping since equal")
                validateListAsserts(listAssertIterator, containerElement, data, done, stepDelay)
            }
        } else {
            console.info("all validateListAsserts are done")
            done()
        }
    }

    private val HTMLElement.styleContent: String
        get() = (getAttribute("style") ?: "") + children.toList().map { it.styleContent }.joinToString("")

    private fun getChildIdsAsString(containerElement: HTMLElement): String {
        return containerElement.children.toList().map { it.getAttribute("id")}.filterNotNull().joinToString(",")
    }
}
