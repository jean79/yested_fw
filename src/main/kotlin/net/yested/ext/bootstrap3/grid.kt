package net.yested.ext.bootstrap3

import net.yested.core.html.div
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

interface ColumnDefinition {
    val code: String
    val width: Int
}

sealed class Col {
    class Width {
        class Xs(override val width: Int) : ColumnDefinition {
            override val code: String = "xs"
        }

        class Sm(override val width: Int) : ColumnDefinition {
            override val code: String = "sm"
        }

        class Md(override val width: Int) : ColumnDefinition {
            override val code: String = "md"
        }

        class Lg(override val width: Int) : ColumnDefinition {
            override val code: String = "lg"
        }
    }
    class Offset {
        class Xs(override val width: Int) : ColumnDefinition {
            override val code: String = "xs-offset"
        }

        class Sm(override val width: Int) : ColumnDefinition {
            override val code: String = "sm-offset"
        }

        class Md(override val width: Int) : ColumnDefinition {
            override val code: String = "md-offset"
        }

        class Lg(override val width: Int) : ColumnDefinition {
            override val code: String = "lg-offset"
        }
    }
    class Push {
        class Xs(override val width: Int) : ColumnDefinition {
            override val code: String = "xs-push"
        }

        class Sm(override val width: Int) : ColumnDefinition {
            override val code: String = "sm-push"
        }

        class Md(override val width: Int) : ColumnDefinition {
            override val code: String = "md-push"
        }

        class Lg(override val width: Int) : ColumnDefinition {
            override val code: String = "lg-push"
        }
    }
    class Pull {
        class Xs(override val width: Int) : ColumnDefinition {
            override val code: String = "xs-pull"
        }

        class Sm(override val width: Int) : ColumnDefinition {
            override val code: String = "sm-pull"
        }

        class Md(override val width: Int) : ColumnDefinition {
            override val code: String = "md-pull"
        }

        class Lg(override val width: Int) : ColumnDefinition {
            override val code: String = "lg-pull"
        }
    }
}

fun ColumnDefinition.css() = "col-$code-$width"

fun HTMLElement.row(init:HTMLDivElement.()->Unit) {
    div { className = "row"
        init()
    }
}

fun HTMLElement.col(vararg width: ColumnDefinition, init: HTMLDivElement.()->Unit) {
    div { className = width.map { it.css() }.joinToString(separator = " ")
        init()
    }
}