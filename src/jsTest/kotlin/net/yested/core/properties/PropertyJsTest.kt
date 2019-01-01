package net.yested.core.properties

import kotlinx.coroutines.*
import kotlin.test.Test
import spec.*

@Suppress("unused")
/**
 * A test for [Property].
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 9/29/16
 * Time: 1:51 PM
 */
class PropertyJsTest {
    @Test
    fun async() {
        GlobalScope.promise {
            val property = 100.toProperty()
            val asyncProperty = property.async()
            asyncProperty.get().mustBe(100)

            property.set(200)
            asyncProperty.get().mustBe(100)

            repeatWithDelayUntil({ asyncProperty.get() == 200 }, 2) {}
            asyncProperty.get().mustBe(200)
        }
    }

    private suspend fun repeatWithDelayUntil(check: () -> Boolean, millisecondInterval: Int, run: () -> Unit) {
        if (check()) {
            run()
        } else {
            delay(millisecondInterval.toLong())
            repeatWithDelayUntil(check, millisecondInterval, run)
        }
    }
}
