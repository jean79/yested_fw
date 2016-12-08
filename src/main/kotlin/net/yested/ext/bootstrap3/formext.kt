package net.yested.ext.bootstrap3

import net.yested.core.html.p
import net.yested.core.properties.ReadOnlyProperty
import org.w3c.dom.*
import kotlin.dom.appendText

class BtsFormItemContext(val labelId: String, val labelElement: HTMLLabelElement, val inputElement: HTMLDivElement) {

    fun btsFormLabel(init: HTMLLabelElement.()->Unit) {
        labelElement.init()
    }

    fun btsFormInput(init: HTMLDivElement.()->Unit) {
        inputElement.init()
    }
}

private var labelIdSequence = 0

class BtsFormContext(
        val element: HTMLFormElement,
        val format: FormFormat,
        val labelWidth: ColumnDefinition? = null,
        val inputWidth: ColumnDefinition? = null) {

    fun btsFormItem(
            labelId: String = "${++labelIdSequence}",
            state: ReadOnlyProperty<State>,
            init: BtsFormItemContext.() -> Unit) {

        element.formGroup(state = state) {
            val labelElement = btsLabel(width = labelWidth, htmlFor = labelId) {}
            val inputElement = if (format == FormFormat.Horizontal) {
                col(width = inputWidth!!) {}
            } else {
                this
            }
            BtsFormItemContext(labelId = labelId, labelElement = labelElement, inputElement = inputElement)
                    .init()
        }
    }

    fun btsFormItemSimple(
            labelId: String = "${++labelIdSequence}",
            state: ReadOnlyProperty<State>,
            label: String = "",
            init: HTMLDivElement.(labelId: String) -> Unit) {

        element.formGroup(state = state) {
            btsLabel(width = labelWidth, htmlFor = labelId) {
                appendText(label)
            }
            if (format == FormFormat.Horizontal) {
                col(width = inputWidth!!) {
                    init(labelId)
                }
            } else {
                init(labelId)
            }
        }
    }

    fun btsFormStatic(label: String = "", init: HTMLParagraphElement.() -> Unit) {

        element.formGroup {
            btsLabel(width = labelWidth) {
                appendText(label)
            }
            if (format == FormFormat.Horizontal) {
                col(width = inputWidth!!) {
                    p { className = "form-control-static"
                        init()
                    }
                }
            } else {
                p { className = "form-control-static"
                    init()
                }
            }
        }

    }

}

fun HTMLElement.btsFormDefault(init: BtsFormContext.()->Unit) {
    btsForm(format = FormFormat.Default) {
        BtsFormContext(element = this, format = FormFormat.Default).init()
    }
}

fun HTMLElement.btsFormInline(init: BtsFormContext.()->Unit) {
    btsForm(format = FormFormat.Inline) {
        BtsFormContext(element = this, format = FormFormat.Inline).init()
    }
}

fun HTMLElement.btsFormHorizontal(
        labelWidth: ColumnDefinition,
        inputWidth: ColumnDefinition,
        init: BtsFormContext.()->Unit) {
    btsForm(format = FormFormat.Horizontal) {
        BtsFormContext(
                element = this,
                format = FormFormat.Horizontal,
                labelWidth = labelWidth,
                inputWidth = inputWidth).init()
    }
}