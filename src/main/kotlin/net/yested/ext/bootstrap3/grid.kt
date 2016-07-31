package net.yested.ext.bootstrap3

import net.yested.core.html.div
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

interface ColumnDefinition {
    val css: String
}

sealed class Col {
    class Width {
        class Xs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xs-$width"
        }

        class Sm(val width: Int) : ColumnDefinition {
            override val css: String =  "col-sm-$width"
        }

        class Md(val width: Int) : ColumnDefinition {
            override val css: String =  "col-md-$width"
        }

        class Lg(val width: Int) : ColumnDefinition {
            override val css: String =  "col-lg-$width"
        }
    }
    class Offset {
        class Xs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xs-offset-$width"
        }

        class Sm(width: Int) : ColumnDefinition {
            override val css: String =  "col-sm-offset-$width"
        }

        class Md(width: Int) : ColumnDefinition {
            override val css: String =  "col-md-offset-$width"
        }

        class Lg(width: Int) : ColumnDefinition {
            override val css: String =  "col-lg-offset-$width"
        }
    }
    class Push {
        class Xs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xs-push-$width"
        }

        class Sm(width: Int) : ColumnDefinition {
            override val css: String =  "col-sm-push-$width"
        }

        class Md(width: Int) : ColumnDefinition {
            override val css: String =  "col-md-push-$width"
        }

        class Lg(width: Int) : ColumnDefinition {
            override val css: String =  "col-lg-push-$width"
        }
    }
    class Pull {
        class Xs(width: Int) : ColumnDefinition {
            override val css: String =  "col-xs-pull-$width"
        }

        class Sm(width: Int) : ColumnDefinition {
            override val css: String =  "col-sm-pull-$width"
        }

        class Md(width: Int) : ColumnDefinition {
            override val css: String =  "col-md-pull-$width"
        }

        class Lg(width: Int) : ColumnDefinition {
            override val css: String =  "col-lg-pull-$width"
        }
    }
}

infix fun ColumnDefinition.and(other: ColumnDefinition) = object: ColumnDefinition {
    override val css: String = this@and.css + other.css
}
        
fun HTMLElement.row(init:HTMLDivElement.()->Unit) {
    div { className = "row"
        init()
    }
}

fun HTMLElement.col(width: ColumnDefinition, init: HTMLDivElement.()->Unit) {
    div { className = width.css
        init()
    }
}