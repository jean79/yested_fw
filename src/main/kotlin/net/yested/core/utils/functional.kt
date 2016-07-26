package net.yested.core.utils

infix fun <T> T.with(doWith: T.()->Unit): T {
    this.doWith()
    return this
}