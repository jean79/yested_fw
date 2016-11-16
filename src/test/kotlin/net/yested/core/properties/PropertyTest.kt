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
    fun mapBidirectionally_shouldUpdateTheNewProperty() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.mapBidirectionally(
                transform = { it.toString() }, reverse = { parseInt(it) })
        intProperty.set(456)
        textProperty.get().mustBe("456")
    }

    @Test
    fun mapBidirectionally_shouldUpdateTheOriginalProperty() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.mapBidirectionally(
                transform = { it.toString() }, reverse = { parseInt(it) })
        textProperty.set("456")
        intProperty.get().mustBe(456)
    }

    @Test
    fun mapBidirectionally_shouldNotUpdateThePropertyBeingChanged() {
        val intProperty = 123.toProperty()
        val textProperty = intProperty.mapBidirectionally(
                transform = { it.toString() }, reverse = { parseInt(it) })
        textProperty.set("00456")
        intProperty.get().mustBe(456)
        textProperty.get().mustBe("00456")
    }
}