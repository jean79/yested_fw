package spec

import kotlin.test.fail

/**
 * Kotlin convenience methods for tests.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 6/16/16
 * Time: 5:59 AM
 */
fun <T> T.mustBe(expected: T) {
    val equal = equalTo(expected)
    if (!equal) {
        fail("expected '$expected' but actual was '$this'")
    }
}

fun <T> T.mustNotBe(expected: T) {
    val equal = equalTo(expected)
    if (equal) {
        fail("did not expect '$expected' but it was equal: '$this'")
    }
}

private fun <T> T.equalTo(expected: T): Boolean {
    if (this is ArrayList<*>) {
        if (expected is ArrayList<*>) {
            if (size != expected.size) {
                return false
            }
            var index = 0
            do {
                if (!this[index].equalTo(expected[index])) {
                    return false
                }
                index += 1
            } while (index < size)
            return true
        } else {
            return this.toString() == expected.toString()
        }
    } else {
        return this == expected
    }
}

fun String?.mustContain(expectedSubstring: String) {
    val contains = this != null && contains(expectedSubstring)
    if (!contains) {
        fail("expected string containing '$expectedSubstring' but actual was '${this}'")
    }
}
