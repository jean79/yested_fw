package net.yested.ext.bootstrap3

import net.yested.core.html.div
import net.yested.core.html.form
import net.yested.core.html.label
import net.yested.core.properties.ReadOnlyProperty
import net.yested.core.properties.ValidationStatus
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

class State(val status: Status, val errorMessage: String?) {
    companion object {
        val Default = State(Status.Default, null)
    }
}

fun ValidationStatus.toState(): State {
    return State(if (success) Status.Success else Status.Error, errorMessage)
}

enum class Size(val code: String) {
    Large("lg"),
    Small("sm"),
    Default("")
}

fun HTMLElement.formGroup(
        state: ReadOnlyProperty<State> = State(status = Status.Default, errorMessage = null).toProperty(),
        size: Size = Size.Default,
        init: HTMLDivElement.()->Unit): HTMLDivElement {
    return div {
        state.onNext {
            className = "form-group ${it.status.code} form-group-${size.code}"
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

fun HTMLElement.btsLabel(width: ColumnDefinition? = null, htmlFor: String? = null, init:HTMLLabelElement.()->Unit): HTMLLabelElement {
    return label {
        className =  "control-label ${width?.css ?: ""}"
        htmlFor?.let { this.htmlFor = it}
        init()
    }
}
