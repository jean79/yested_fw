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
