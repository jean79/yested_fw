package net.yested.core.properties

import org.junit.Test
import spec.*

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
    fun bind_shouldUpdateTheNewProperty() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.bind(
                transform = { it.toString() }, reverse = { parseInt(it) })
        intProperty.set(456)
        textProperty.get().mustBe("456")
    }

    @Test
    fun bind_shouldUpdateTheOriginalProperty() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.bind(
                transform = { it.toString() }, reverse = { parseInt(it) })
        textProperty.set("456")
        intProperty.get().mustBe(456)
    }

    @Test
    fun bind_shouldNotUpdateThePropertyBeingChanged() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.bind(
                transform = { it.toString() }, reverse = { parseInt(it) })
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
