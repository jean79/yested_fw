import net.yested.core.html.*
import net.yested.core.properties.*
import net.yested.core.utils.with
import net.yested.ext.bootstrap3.*
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.dom.appendText

enum class City { Prague, London }

fun ReadOnlyProperty<ValidationStatus>.message() =
    this.map {
        if (it.success) {
            State(status = Status.Default, errorMessage = null)
        } else {
            State(status = Status.Error, errorMessage = it.errorMessage)
        }
    }

fun main(args: Array<String>) {

    val p = Property("hello")
    val validation = p.validate(errorMessage = "Name is required") { it.size > 0 }.message()
    val city = Property(City.Prague)
    val active = Property(false)

    console.info("hello")

    val element: HTMLElement = document.getElementById("main").asDynamic()

    element with {
        navbar(inverted = true) {
            brand {
                appendText("Sample Application")
            }
            menu {
                item(active = Property(true)) {
                    a { href = "#firstItem"
                        appendText("First Item")
                    }
                }
                item {
                    a {
                        appendText("Second item")
                    }
                }
                dropDown(label = "Drop") {
                    item {
                        a { appendText("bla") }
                    }
                    separator()
                    dropDownHeader { appendText("subheader") }
                    item {
                        a { appendText("tri") }
                    }
                    item {
                        a { appendText("ctyri") }
                    }
                }
            }
            button(position = NavbarPosition.Right) {
                appendText("Tlacitko")
            }
            text(position = NavbarPosition.Right) {
                appendText("some text")
            }
        }

        container {
            jumbotron {
                h1 {
                    appendText("some header")
                }
                p {
                    appendText("some text")
                }
            }
            row {
                col(Col.Width.Xs(3) and Col.Width.Lg(3)) {
                    appendText("hello")
                }
            }
            p {
                btsForm(format = FormFormat.Horizontal) {
                    formGroup(state = validation) {
                        btsLabel(htmlFor = "ii", width = Col.Width.Lg(4)) {
                            appendText("Label")
                        }
                        col(Col.Width.Lg(4)) {
                            textInput(id = "ii", value = p)
                        }
                    }
                    btsButton { appendText("Submit") }
                }
                btsFormHorizontal(labelWidth = Col.Width.Lg(4), inputWidth = Col.Width.Lg(8)) {
                    btsFormItem(state = validation) {
                        btsFormLabel { appendText("Label") }
                        btsFormInput {
                            textInput(id = labelId, value = p)
                        }
                    }
                    btsFormItemSimple(state = validation, label = "Test2") { labelId ->
                        textInput(id = labelId, value = p)
                    }
                }
            }
            p {
                navTabs {
                    item(active = active) {
                        a { appendText("Jedna") }
                    }
                    item(active = active.not()) {
                        a { appendText("Dva") }
                    }
                }
            }
            p {
                glyphicon(icon = "eur")
            }
            p {
                textInput(value = p, id = "ii") //add validation
            }
            p {
                selectInput(selected = city, options = Property(City.values().toList()), render = { appendText(it.name) })
            }
            p {
                btsButton(look = ButtonLook.Primary, disabled = active.not(), onclick = { p.set("reset") }) {
                     appendText("button text")
                }
                btsButton(active = active) {
                    appendText("selectable")
                }
            }
            p {
                dropdown(label = "dropdown") {
                    item {
                        a { appendText("bla") }
                    }
                    separator()
                    item {
                        a { appendText("dva") }
                    }
                }
            }
            p {
                inputGroup {
                    dropdown(label = "Hello") {
                        item {
                            a { appendText("bla") }
                        }
                    }
                    input {
                        textInput(value = Property("sample"), id = "ii")
                    }
                    addon { glyphicon(icon = "eur") }
                }
            }
            p {
                buttonGroup {
                    button(look = ButtonLook.Primary, disabled = active.not(), onclick = { p.set("reset") }) {
                        appendText("button text")
                    }
                    button(active = active) {
                        appendText("selectable")
                    }
                }
                buttonToolbar {
                    group {
                        button(look = ButtonLook.Primary, disabled = active.not(), onclick = { p.set("reset") }) {
                            appendText("button text")
                        }
                        button(active = active) {
                            appendText("selectable")
                        }
                        dropdown(label = "Ahoj") {
                            item {
                                a { appendText("bla") }
                            }
                            separator()
                            item {
                                a { appendText("dva") }
                            }
                        }
                    }
                    group {
                        button(look = ButtonLook.Primary, disabled = active.not(), onclick = { p.set("reset") }) {
                            appendText("button text")
                        }
                        button(active = active) {
                            appendText("selectable")
                        }
                    }
                }
            }
        }

        /*textInput(value = p, validation = validation)
        div {
            id = "id"
            className = "bla"
            span {
                className = "spanClass"
            }
        }
        table {
            thead {
                tr {
                    th {
                        appendText("Col1")
                    }
                    th {
                        appendText("Col2")
                    }
                }
            }
            tbody {
                tr {
                    td {
                        a {
                            appendText("http://www.seznam.cz")
                            onClick {
                                js("alert('hh')")
                            }
                        }
                    }
                    td {
                        text(p)
                    }
                    td {
                        selectInput(selected = city, options = Property(City.values().toList()), render = { appendText(it.name) })
                        appendText("Selected city: ")
                        text(value = city.map { it.name })
                    }
                }
            }
        }*/
    }

}