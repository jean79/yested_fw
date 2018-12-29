package net.yested.core.html

import org.w3c.dom.*
import kotlin.text.Regex

/*
 * These are performance fixes to the KotlinJS library,
 * which is especially important when they are used as heavily as yested does.
 */
private val regexByCssClass: MutableMap<String,Regex> = mutableMapOf()

private fun toRegex(cssClass: String): Regex = regexByCssClass.getOrPut(cssClass) { """(^| )$cssClass($| )""".toRegex() }

/** Returns true if the element has the given CSS class style in its 'class' attribute */
fun Element.hasClass2(cssClass: String): Boolean = className.contains(toRegex(cssClass))

/**
 * Adds CSS class to element. Has no effect if all specified classes are already in class attribute of the element
 *
 * @return true if at least one class has been added
 */
fun Element.addClass2(vararg cssClasses: String): Boolean {
    val missingClasses = cssClasses.filterNot { hasClass2(it) }
    if (missingClasses.isNotEmpty()) {
        val presentClasses = className.trim()
        className = buildString {
            append(presentClasses)
            if (!presentClasses.isEmpty()) {
                append(" ")
            }
            missingClasses.joinTo(this, " ")
        }
        return true
    }

    return false
}

/**
 * Removes all [cssClasses] from element. Has no effect if all specified classes are missing in class attribute of the element
 *
 * @return true if at least one class has been removed
 */
fun Element.removeClass2(vararg cssClasses: String): Boolean {
    if (cssClasses.any { hasClass2(it) }) {
        var result: String = className
        cssClasses.forEach {
            result = result.replace(toRegex(it), " ")
        }
        className = result.trim()
        return true
    }

    return false
}
