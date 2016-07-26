import net.yested.core.html.*
import net.yested.core.properties.*
import net.yested.core.utils.with
import net.yested.ext.bootstrap3.*
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.dom.plus

enum class City { Prague, London }

fun main(args: Array<String>) {

    val p = Property("hello")
    val validation = p.validate(errorMessage = "Name is required") { it.size > 0 } debug { "validator: $it" }
    val city = Property(City.Prague)
    val active = Property(false)

    console.info("hello")

    val element: HTMLElement = document.getElementById("main").asDynamic()

    element with {
        navbar(inverted = true) {
            brand {
                plus("Sample Application")
            }
            menu {
                item(active = Property(true)) {
                    a { href = "#firstItem"
                        plus("First Item")
                    }
                }
                item {
                    a {
                        plus("Second item")
                    }
                }
                dropDown(label = "Drop") {
                    item {
                        a { plus("bla") }
                    }
                    separator()
                    dropDownHeader { plus("subheader") }
                    item {
                        a { plus("tri") }
                    }
                    item {
                        a { plus("ctyri") }
                    }
                }
            }
            button(position = NavbarPosition.Right) {
                plus("Tlacitko")
            }
            text(position = NavbarPosition.Right) {
                plus("some text")
            }
        }

        container {
            jumbotron {
                h1 {
                    plus("some header")
                }
                p {
                    plus("some text")
                }
            }
            row {
                col(Col.Width.Xs(3), Col.Width.Lg(3)) {
                    plus("ahoj")
                }
            }
            p {
                btsForm(format = FormFormat.Horizontal) {
                    formGroup {
                        label { className = Col.Width.Lg(4).css()
                            plus("Label")
                        }
                        col(Col.Width.Lg(4)) {
                            textInput(value = Property("Hello"), className = "form-control".toProperty())
                        }
                    }
                    btsButton { plus("Submit") }
                }
            }
            p {
                navTabs {
                    item(active = active) {
                        a { plus("Jedna") }
                    }
                    item(active = active.not()) {
                        a { plus("Dva") }
                    }
                }
            }
            p {
                glyphicon(icon = "eur")
            }
            p {
                textInput(value = p, validation = validation)
            }
            p {
                selectInput(selected = city, options = Property(City.values().toList()), render = { plus(it.name) })
            }
            p {
                btsButton(look = ButtonLook.Primary, disabled = active.not(), onclick = { p.set("reset") }) {
                     plus("button text")
                }
                btsButton(active = active) {
                    plus("selectable")
                }
            }
            p {
                dropdown(label = "dropdown") {
                    item {
                        a { plus("bla") }
                    }
                    separator()
                    item {
                        a { plus("dva") }
                    }
                }
            }
            p {
                inputGroup {
                    dropdown(label = "Hello") {
                        item {
                            a { plus("bla") }
                        }
                    }
                    input {
                        textInput(value = Property("sample"), className = Property("form-control"))
                    }
                    addon { glyphicon(icon = "eur") }
                }
            }
            p {
                buttonGroup {
                    button(look = ButtonLook.Primary, disabled = active.not(), onclick = { p.set("reset") }) {
                        plus("button text")
                    }
                    button(active = active) {
                        plus("selectable")
                    }
                }
                buttonToolbar {
                    group {
                        button(look = ButtonLook.Primary, disabled = active.not(), onclick = { p.set("reset") }) {
                            plus("button text")
                        }
                        button(active = active) {
                            plus("selectable")
                        }
                        dropdown(label = "Ahoj") {
                            item {
                                a { plus("bla") }
                            }
                            separator()
                            item {
                                a { plus("dva") }
                            }
                        }
                    }
                    group {
                        button(look = ButtonLook.Primary, disabled = active.not(), onclick = { p.set("reset") }) {
                            plus("button text")
                        }
                        button(active = active) {
                            plus("selectable")
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
                        plus("Col1")
                    }
                    th {
                        plus("Col2")
                    }
                }
            }
            tbody {
                tr {
                    td {
                        a {
                            plus("http://www.seznam.cz")
                            onClick {
                                js("alert('hh')")
                            }
                        }
                    }
                    td {
                        text(p)
                    }
                    td {
                        selectInput(selected = city, options = Property(City.values().toList()), render = { plus(it.name) })
                        plus("Selected city: ")
                        text(value = city.map { it.name })
                    }
                }
            }
        }*/
    }

}