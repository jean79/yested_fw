package net.yested.core.html

import net.yested.core.properties.*
import net.yested.core.utils.Div
import org.junit.Test
import org.w3c.dom.HTMLCollection
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLTableElement
import spec.*
import kotlin.dom.appendText

/**
 * A test for [tbody].
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 9/29/16
 * Time: 1:51 PM
 */
class HtmlBindTest {
    @Test
    fun tableShouldReflectData() {
        val data: Property<List<Int>?> = listOf(1, 2, 3).toProperty()
        var table: HTMLTableElement? = null
        var nextId = 1
        Div {
            table = table {
                thead {
                    th {
                        appendText("Items")
                    }
                }
                tbody(data) { item ->
                    tr { id = (nextId++).toString()
                        td { appendText(item.toString()) }
                    }
                }
            }
        }
        table!!.textContent.mustBe("Items123")

        data.set(listOf(1, 2, 3))
        table!!.textContent.mustBe("Items123")
        getRowIdsAsString(table).mustBe("1,2,3")

        data.set(listOf(2, 3, 1))
        table!!.textContent.mustBe("Items231")
        getRowIdsAsString(table).mustBe("2,3,4")

        data.set(listOf(1, 2, 3))
        table!!.textContent.mustBe("Items123")
        getRowIdsAsString(table).mustBe("6,2,3")

        data.set(listOf(1, 3, 2))
        table!!.textContent.mustBe("Items132")
        getRowIdsAsString(table).mustBe("5,6,2")

        data.set(listOf(1, 2, 3, 4, 5, 6, 7, 8))
        table!!.textContent.mustBe("Items12345678")
        getRowIdsAsString(table).mustBe("5,2,7,8,9,10,11,12")

        data.set(listOf(1, 2, 3, 8, 7, 4, 5, 6))
        table!!.textContent.mustBe("Items12387456")
        getRowIdsAsString(table).mustBe("5,2,7,13,14,8,9,10")

        data.set(listOf(1, 2, 3, 4, 5, 6, 7, 8))
        table!!.textContent.mustBe("Items12345678")
        getRowIdsAsString(table).mustBe("5,2,7,8,9,10,15,16")

        data.set(listOf(1, 2, 3, 4, 6, 7, 8))
        table!!.textContent.mustBe("Items1234678")
        getRowIdsAsString(table).mustBe("5,2,7,8,10,15,16")

        data.set(listOf(1, 2, 3, 7, 8))
        table!!.textContent.mustBe("Items12378")
        getRowIdsAsString(table).mustBe("5,2,7,15,16")

        data.set(listOf(8, 7, 3, 2, 1))
        table!!.textContent.mustBe("Items87321")
        getRowIdsAsString(table).mustBe("16,15,7,2,5")

        data.set(listOf(10, 11, 12, 13))
        table!!.textContent.mustBe("Items10111213")
        getRowIdsAsString(table).mustBe("17,18,19,20")
    }

    private fun getRowIdsAsString(table: HTMLTableElement?): String {
        return (table!!.lastChild!! as HTMLElement).children.toList().map { it.getAttribute("id")}.joinToString(",")
    }
}

fun HTMLCollection.toList(): List<HTMLElement> {
    return (0..(this.length - 1)).map { item(it)!! as HTMLElement }
}
