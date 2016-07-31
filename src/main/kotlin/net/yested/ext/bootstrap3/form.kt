package net.yested.ext.bootstrap3

import net.yested.core.html.div
import net.yested.core.html.form
import net.yested.core.html.label
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.properties.toProperty
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLLabelElement

enum class Status(val code: String) {
    Default(""),
    Success("has-success"),
    Warning("has-warning"),
    Error("has-error"),
}

class State(
        val status: Status,
        val errorMessage: String?
)

fun HTMLElement.formGroup(
        state: ReadOnlyProperty<State> = State(status = Status.Default, errorMessage = null).toProperty(),
        init: HTMLDivElement.()->Unit) {
    div {
        state.onNext {
            className = "form-group ${it.status.code}"
        }
        init()
    }
}

enum class FormFormat(val code: String) {
    Default(""),
    Horizontal("form-horizontal"),
    Inline("form-inline")
}

fun HTMLElement.btsForm(format: FormFormat = FormFormat.Default, init: HTMLFormElement.()->Unit) {
    form { className = format.code
        init()
    }
}

fun HTMLElement.btsLabel(width: ColumnDefinition? = null, htmlFor: String? = null, init:HTMLLabelElement.()->Unit) {
    label {
        className =  "control-label ${width?.let { width.css} }"
        htmlFor?.let { this.htmlFor = it}
        init()
    }
}