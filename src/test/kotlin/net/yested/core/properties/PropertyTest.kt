package net.yested.core.properties

import kotlin.test.Test
import spec.*
import kotlin.js.Math

/**
 * A test for [Property].
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 9/29/16
 * Time: 1:51 PM
 */
class PropertyTest {
    @Test
    fun onNext_shouldIgnoreSetWithSameValue() {
        val property = 123.toProperty()
        var onNextCount = 0
        property.onNext { onNextCount++ }
        onNextCount.mustBe(1)

        property.set(123)
        onNextCount.mustBe(1)
    }

    @Test
    fun onNext_shouldPropagateSetWithDifferentHashCode() {
        val list = mutableListOf(1, 2, 3)
        val property = list.toProperty()
        var onNextCount = 0
        property.onNext { onNextCount++ }
        onNextCount.mustBe(1)

        list.add(4)
        property.set(list)
        onNextCount.mustBe(2)
    }

    @Test
    fun onNext_shouldIgnoreSetWithSameHashCode() {
        val list = mutableListOf(1, 2, 3)
        val property = list.toProperty()
        var onNextCount = 0
        property.onNext { onNextCount++ }
        onNextCount.mustBe(1)

        property.set(list)
        onNextCount.mustBe(1)
    }

    @Test
    fun onNext_reentrantAvoidsInfiniteLoopForSameValue() {
        val property = 123.toProperty()
        property.onNext { property.set(300) }
        property.get().mustBe(300)
    }

    @Test
    fun onNext_reentrantSetNewValue() {
        val property = 123.toProperty()
        val values1 = mutableListOf<Int>()
        val values2 = mutableListOf<Int>()
        property.onNext { values1.add(it) }
        property.onNext { if (property.get() == 200) property.set(300) }
        property.onNext { values2.add(it) }

        property.set(200)
        values1.mustBe(mutableListOf(123, 200, 300))
        values2.mustBe(mutableListOf(123, 300))
    }

    @Test
    fun onNext_reentrantSetNewValueHash() {
        val list = mutableListOf(1, 2, 3)
        val property = list.toProperty()
        val values1 = mutableListOf<MutableList<Int>>()
        val values2 = mutableListOf<MutableList<Int>>()
        property.onNext { values1.add(it) }
        property.onNext { if (values1.size == 2) {
            list.add(400)
            property.set(list)
        } }
        property.onNext { values2.add(it) }

        list.add(200)
        property.set(list)
        values1.mustBe(mutableListOf(list, list, list))
        values2.mustBe(mutableListOf(list, list))
    }

    @Test
    fun onNext_reentrantModifyValueHash() {
        val list = mutableListOf(1, 2, 3)
        val property = list.toProperty()
        val values1 = mutableListOf<MutableList<Int>>()
        val values2 = mutableListOf<MutableList<Int>>()
        property.onNext { values1.add(it) }
        property.onNext { if (values1.size == 2) {
            list.add(400)
        } }
        property.onNext { values2.add(it) }

        list.add(200)
        property.set(list)
        values1.mustBe(mutableListOf(list, list))
        values2.mustBe(mutableListOf(list, list))
    }

    @Test
    fun onChange() {
        val property = 100.toProperty()
        val changes = mutableListOf<Pair<Int,Int>>()
        val disposable = property.onChange { old, value -> changes.add(Pair(old, value)) }
        changes.mustBe(emptyList<Pair<Int,Int>>())

        property.set(100)
        changes.mustBe(emptyList<Pair<Int,Int>>())

        property.set(200)
        changes.mustBe(listOf(Pair(100, 200)))

        property.set(300)
        changes.mustBe(listOf(Pair(100, 200), Pair(200, 300)))

        disposable.dispose()
        property.set(400)
        changes.mustBe(listOf(Pair(100, 200), Pair(200, 300)))
    }

    @Test
    fun zip() {
        val int1Property = 123.toProperty()
        val int2Property = 456.toProperty()
        val textProperty = int1Property.zip(int2Property).map { pair ->
            val (int1, int2) = pair
            "" + int1 + int2
        }
        textProperty.get().mustBe("123456")

        int1Property.set(999)
        textProperty.get().mustBe("999456")

        int2Property.set(555)
        textProperty.get().mustBe("999555")
    }

    @Test
    fun zip_triple() {
        val int1Property = 123.toProperty()
        val int2Property = 456.toProperty()
        val stringProperty = "hello".toProperty()
        val textProperty = int1Property.zip(int2Property, stringProperty).map { triple ->
            val (int1, int2, string) = triple
            "" + int1 + int2 + string
        }
        textProperty.get().mustBe("123456hello")

        int1Property.set(999)
        textProperty.get().mustBe("999456hello")

        int2Property.set(555)
        textProperty.get().mustBe("999555hello")

        stringProperty.set("bye")
        textProperty.get().mustBe("999555bye")
    }

    @Test
    fun zip4() {
        val int1Property = 123.toProperty()
        val int2Property = 456.toProperty()
        val stringProperty = "hello".toProperty()
        val int4Property = 1234.toProperty()
        val textProperty = int1Property.zip(int2Property, stringProperty, int4Property).map { tuple ->
            val (int1, int2, string, int4) = tuple
            "" + int1 + int2 + string + int4
        }
        textProperty.get().mustBe("123456hello1234")

        int1Property.set(999)
        textProperty.get().mustBe("999456hello1234")

        int2Property.set(555)
        textProperty.get().mustBe("999555hello1234")

        stringProperty.set("bye")
        textProperty.get().mustBe("999555bye1234")

        int4Property.set(555)
        textProperty.get().mustBe("999555bye555")
    }

    @Test
    fun zip5() {
        val int1Property = 123.toProperty()
        val int2Property = 456.toProperty()
        val stringProperty = "hello".toProperty()
        val int4Property = 1234.toProperty()
        val int5Property = 555.toProperty()
        val textProperty = int1Property.zip(int2Property, stringProperty, int4Property, int5Property).map { tuple ->
            val (int1, int2, string, int4, int5) = tuple
            "" + int1 + int2 + string + int4 + int5
        }
        textProperty.get().mustBe("123456hello1234555")

        int1Property.set(999)
        textProperty.get().mustBe("999456hello1234555")

        int2Property.set(555)
        textProperty.get().mustBe("999555hello1234555")

        stringProperty.set("bye")
        textProperty.get().mustBe("999555bye1234555")

        int4Property.set(444)
        textProperty.get().mustBe("999555bye444555")

        int5Property.set(5)
        textProperty.get().mustBe("999555bye4445")
    }

    @Test
    fun zip6() {
        val int1Property = 123.toProperty()
        val int2Property = 456.toProperty()
        val stringProperty = "hello".toProperty()
        val int4Property = 1234.toProperty()
        val int5Property = 555.toProperty()
        val int6Property = 666.toProperty()
        val textProperty = int1Property.zip(int2Property, stringProperty, int4Property, int5Property, int6Property).map { tuple ->
            val (int1, int2, string, int4, int5, int6) = tuple
            "" + int1 + int2 + string + int4 + int5 + int6
        }
        textProperty.get().mustBe("123456hello1234555666")

        int1Property.set(999)
        textProperty.get().mustBe("999456hello1234555666")

        int2Property.set(555)
        textProperty.get().mustBe("999555hello1234555666")

        stringProperty.set("bye")
        textProperty.get().mustBe("999555bye1234555666")

        int4Property.set(444)
        textProperty.get().mustBe("999555bye444555666")

        int5Property.set(5)
        textProperty.get().mustBe("999555bye4445666")

        int6Property.set(6)
        textProperty.get().mustBe("999555bye44456")
    }

    @Test
    fun collect() {
        val property = 123.toProperty()
        val sumAccumulator = property.collect<Int,Int> { collected, value -> (collected ?: 0) + value }
        sumAccumulator.get().mustBe(123)

        property.set(3)
        sumAccumulator.get().mustBe(126)

        property.set(4)
        sumAccumulator.get().mustBe(130)
    }

    @Test
    fun collect_nullable() {
        val property = 123.toProperty()
        val maxEven = property.collect<Int?,Int> { collected, value ->
            if (value % 2 != 0) collected else if (collected == null) value else Math.max(value, collected)
        }
        maxEven.get().mustBe(null)

        property.set(3)
        maxEven.get().mustBe(null)

        property.set(4)
        maxEven.get().mustBe(4)

        property.set(2)
        maxEven.get().mustBe(4)

        property.set(10)
        maxEven.get().mustBe(10)
    }

    @Test
    fun flatMap() {
        val propertyByKey = mapOf(1 to Property("Julie"), 2 to Property("Sam"))
        val intProperty = Property(1)
        val nameProperty = intProperty.flatMap { propertyByKey.get(it)!! }
        nameProperty.get().mustBe("Julie")

        intProperty.set(2)
        nameProperty.get().mustBe("Sam")
        propertyByKey.get(1)!!.listenerCount.mustBe(0)

        propertyByKey.get(2)!!.set("George")
        nameProperty.get().mustBe("George")

        propertyByKey.get(1)!!.set("Athena")
        nameProperty.get().mustBe("George")

        intProperty.set(1)
        nameProperty.get().mustBe("Athena")
        propertyByKey.get(2)!!.listenerCount.mustBe(0)

        intProperty.listenerCount.mustBe(1)
        propertyByKey.get(1)!!.listenerCount.mustBe(1)
        propertyByKey.get(1)!!.listenerCount.mustBe(1)
        propertyByKey.get(2)!!.listenerCount.mustBe(0)
    }

    @Test
    fun flatMapOrNull() {
        val propertyByKey = mapOf("A" to Property("Julie"), "B" to Property("Sam"))
        val listProperty = Property(listOf("A"))
        val flatMapProperty = listProperty.flatMapOrNull { it.firstOrNull()?.let { propertyByKey[it] } }
        flatMapProperty.get().mustBe("Julie")

        listProperty.set(emptyList())
        flatMapProperty.get().mustBe(null)

        listProperty.set(listOf("A"))
        flatMapProperty.get().mustBe("Julie")

        propertyByKey.get("A")!!.set("Athena")
        flatMapProperty.get().mustBe("Athena")
    }

    @Test
    fun mapWith() {
        val int1Property = 123.toProperty()
        val int2Property = 456.toProperty()
        val textProperty = int1Property.mapWith(int2Property) { int1, int2 ->
            "" + int1 + int2
        }
        textProperty.get().mustBe("123456")

        int1Property.set(999)
        textProperty.get().mustBe("999456")

        int2Property.set(555)
        textProperty.get().mustBe("999555")
    }

    @Test
    fun mapWith_2() {
        val int1Property = 123.toProperty()
        val int2Property = 456.toProperty()
        val int3Property = 789.toProperty()
        val textProperty = int1Property.mapWith(int2Property, int3Property) { int1, int2, int3 ->
            "" + int1 + int2 + int3
        }
        textProperty.get().mustBe("123456789")

        int1Property.set(999)
        textProperty.get().mustBe("999456789")

        int3Property.set(555)
        textProperty.get().mustBe("999456555")
    }

    @Test
    fun sortedWith() {
        val listProperty = listOf(3, 1, 2).toProperty()
        val comparatorProperty = Property<Comparator<Int>?>(null)
        val sortedListProperty = listProperty.sortedWith(comparatorProperty)
        sortedListProperty.get().mustBe(listOf(3, 1, 2))

        comparatorProperty.set(Comparator<Int> { obj1, obj2 -> obj1 - obj2 })
        sortedListProperty.get().mustBe(listOf(1, 2, 3))

        comparatorProperty.set(Comparator<Int> { obj1, obj2 -> obj2 - obj1 })
        sortedListProperty.get().mustBe(listOf(3, 2, 1))

        comparatorProperty.set(null)
        sortedListProperty.get().mustBe(listOf(3, 1, 2))
    }

    @Test
    fun sortedWith_listOfProperties_replaceEntry() {
        val one = 1.toProperty()
        val two = 2.toProperty()
        val three = 3.toProperty()
        val listProperty = listOf(three, one, two).toProperty()
        val comparatorProperty = Property(Comparator<Property<Int>> { obj1, obj2 -> obj1.get() - obj2.get() })
        val sortedListProperty = listProperty.sortedWith(comparatorProperty)
        sortedListProperty.get().mustBe(listOf(one, two, three))

        val four = 4.toProperty()
        listProperty.set(listOf(four, one, two))
        sortedListProperty.get().mustBe(listOf(one, two, four))
    }

    fun <T> Property<T>.detectContentChange() {
        set(get())
    }

    @Test
    fun sortedWith_listOfProperties_modifyProperty() {
        val one = 1.toProperty()
        val x = 2.toProperty()
        val three = 3.toProperty()
        val listProperty = listOf(three, one, x).toProperty()
        val comparatorProperty = Property<Comparator<Property<Int>>?>(null)
        val sortedListProperty = listProperty.sortedWith(comparatorProperty)
        sortedListProperty.get().mustBe(listOf(three, one, x))

        comparatorProperty.set(Comparator<Property<Int>> { obj1, obj2 -> obj1.get() - obj2.get() })
        sortedListProperty.get().mustBe(listOf(one, x, three))

        var onNextCount = 0
        sortedListProperty.onNext { onNextCount++ }

        // unfortunately (but better for performance), this won't cause the list to be resorted.
        x.set(0)
        sortedListProperty.get().mustBe(listOf(one, x, three))
        onNextCount.mustBe(1)

        // this should cause the list to be resorted since its value hash has changed
        listProperty.detectContentChange()
        sortedListProperty.get().mustBe(listOf(x, one, three))
        onNextCount.mustBe(2)
    }

    @Test
    fun pairOfProperties_detectChange() {
        val one = 1.toProperty()
        val x = 2.toProperty()
        val pairProperty = Pair(one, x).toProperty()

        var onNextCount = 0
        pairProperty.onNext { onNextCount++ }
        onNextCount.mustBe(1)

        // unfortunately (but better for performance), this won't cause the list to be resorted.
        x.set(0)
        pairProperty.get()
        onNextCount.mustBe(1)

        // pinging the property should cause it to be republished since its value hash has changed
        pairProperty.set(pairProperty.get())
        onNextCount.mustBe(2)
    }

    @Test
    fun bind_shouldUpdateTheNewProperty() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.bind(
                transform = { it.toString() }, reverse = { it.toInt() })
        intProperty.set(456)
        textProperty.get().mustBe("456")
    }

    @Test
    fun bind_shouldUpdateTheOriginalProperty() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.bind(
                transform = { it.toString() }, reverse = { it.toInt() })
        textProperty.set("456")
        intProperty.get().mustBe(456)
    }

    @Test
    fun bind_shouldNotUpdateThePropertyBeingChanged() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.bind(
                transform = { it.toString() }, reverse = { it.toInt() })
        textProperty.set("00456")
        intProperty.get().mustBe(456)
        textProperty.get().mustBe("00456")
    }

    @Test
    fun bindParts_shouldUpdateTheNewProperties() {
        val intProperty = 123.toProperty()
        val (negativeProperty, absProperty) = intProperty.bindParts(
                { it < 0.00 }, { it.abs() },
                { negative, abs -> abs * (if (negative) -1 else 1) })
        intProperty.set(-456)
        negativeProperty.get().mustBe(true)
        absProperty.get().mustBe(456)
    }

    @Test
    fun bindParts_shouldUpdateTheOriginalProperty() {
        val intProperty = 123.toProperty()
        val (negativeProperty, absProperty) = intProperty.bindParts(
                { it < 0.00 }, { it.abs() },
                { negative, abs -> abs * (if (negative) -1 else 1) })
        negativeProperty.set(true)
        intProperty.get().mustBe(-123)

        absProperty.set(342)
        intProperty.get().mustBe(-342)

        negativeProperty.set(false)
        intProperty.get().mustBe(342)
    }

    fun Int.abs(): Int = if (this < 0) -1 * this else this
}
