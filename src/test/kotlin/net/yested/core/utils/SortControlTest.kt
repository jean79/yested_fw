package net.yested.core.utils

import net.yested.core.html.*
import net.yested.core.properties.*
import org.junit.Test
import spec.*
import kotlin.comparisons.compareBy
import kotlin.dom.appendText

/**
 * A test for [sortControl].
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 9/29/16
 * Time: 1:51 PM
 */
class SortControlTest {
    data class Person(val name: String, val age: Int)

    @Test
    fun shouldSortAsExpected() {
        var nameSort : Property<Boolean?>? = null
        var ageSort : Property<Boolean?>? = null
        val currentSort = Property<SortSpecification<Person>?>(null)
        val data = listOf(Person("George", 40), Person("Ancient Billy", 90), Person("Buddy", 7)).toProperty()
        val sortedData = data.sortedWith(currentSort)
        Div {
            table {
                thead {
                    th {
                        nameSort = sortControl(currentSort, compareBy { it.name }) {
                            appendText("Name")
                        }
                    }
                    th {
                        ageSort = sortControl(currentSort, compareBy { it.age }) {
                            appendText("Age")
                        }
                    }
                }
                tbody(sortedData) { item ->
                    tr {
                        td { appendText(item.name) }
                        td { appendText(item.age.toString()) }
                    }
                }
            }
        }
        sortedData.get()?.map { it.name }.mustBe(listOf("George", "Ancient Billy", "Buddy"))

        nameSort!!.set(true)
        sortedData.get()?.map { it.name }.mustBe(listOf("Ancient Billy", "Buddy", "George"))

        ageSort!!.set(true)
        nameSort!!.get().mustBe(null)
        sortedData.get()?.map { it.name }.mustBe(listOf("Buddy", "George", "Ancient Billy"))

        nameSort!!.get().mustBe(null)
        nameSort!!.set(null) // should be a no-op since already null
        sortedData.get()?.map { it.name }.mustBe(listOf("Buddy", "George", "Ancient Billy"))

        nameSort!!.set(false)
        ageSort!!.get().mustBe(null)
        sortedData.get()?.map { it.name }.mustBe(listOf("George", "Buddy", "Ancient Billy"))

        nameSort!!.set(true)
        sortedData.get()?.map { it.name }.mustBe(listOf("Ancient Billy", "Buddy", "George"))

        nameSort!!.set(false)
        sortedData.get()?.map { it.name }.mustBe(listOf("George", "Buddy", "Ancient Billy"))

        nameSort!!.set(null)
        sortedData.get()?.map { it.name }.mustBe(listOf("George", "Ancient Billy", "Buddy"))
    }

    @Test
    fun shouldSortPropertyListAsExpected() {
        var nameSort : Property<Boolean?>? = null
        var ageSort : Property<Boolean?>? = null
        val currentSort = Property<SortSpecification<ReadOnlyProperty<Person>>?>(null)
        val data = listOf(Person("George", 40), Person("Ancient Billy", 90), Person("Buddy", 7)).map { it.toProperty() }.toProperty()
        val sortedData = data.sortedWith(currentSort)
        Div {
            table {
                thead {
                    th {
                        nameSort = sortControl(currentSort, compareByProperty { it.name }) {
                            appendText("Name")
                        }
                    }
                    th {
                        ageSort = sortControl(currentSort, compareByProperty { it.age }) {
                            appendText("Age")
                        }
                    }
                }
                tbody(sortedData) { item ->
                    tr {
                        td { item.onNext { textContent = it.name } }
                        td { item.onNext { textContent = it.age.toString() } }
                    }
                }
            }
        }
        sortedData.get()?.map { it.get().name }.mustBe(listOf("George", "Ancient Billy", "Buddy"))

        nameSort!!.set(true)
        sortedData.get()?.map { it.get().name }.mustBe(listOf("Ancient Billy", "Buddy", "George"))

        ageSort!!.set(true)
        nameSort!!.get().mustBe(null)
        sortedData.get()?.map { it.get().name }.mustBe(listOf("Buddy", "George", "Ancient Billy"))

        nameSort!!.get().mustBe(null)
        nameSort!!.set(null) // should be a no-op since already null
        sortedData.get()?.map { it.get().name }.mustBe(listOf("Buddy", "George", "Ancient Billy"))

        nameSort!!.set(false)
        ageSort!!.get().mustBe(null)
        sortedData.get()?.map { it.get().name }.mustBe(listOf("George", "Buddy", "Ancient Billy"))

        nameSort!!.set(true)
        sortedData.get()?.map { it.get().name }.mustBe(listOf("Ancient Billy", "Buddy", "George"))

        nameSort!!.set(false)
        sortedData.get()?.map { it.get().name }.mustBe(listOf("George", "Buddy", "Ancient Billy"))

        nameSort!!.set(null)
        sortedData.get()?.map { it.get().name }.mustBe(listOf("George", "Ancient Billy", "Buddy"))
    }
}
