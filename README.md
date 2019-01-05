# What is the Yested Framework
Yested Framework is designed for the strongly typed development of Single Page Applications running in a browser. Application and framework are developed in [Kotlin](http://www.kotlinlang.org) language.

# Original version
This repository hosts a new version of Yested Framework.

The old one (https://github.com/jean79/yested) is deprecated.

The main difference to the old Yested framework is the introduction of bindable properties and View Models (from WPF):
https://github.com/jean79/yested_fw/blob/master/src/commonMain/kotlin/net/yested/core/properties/properties.kt

Also, I have removed the Component interface and utilize what was already developed by Kotlin guys:
https://github.com/jean79/yested_fw/blob/master/src/jsMain/kotlin/net/yested/core/html/html.kt

This new approach is much simpler than the first version of Yested. 
It beats in productivity compared to any other UI development framework, including WPF and JavaFX.

# Main features
* Strongly typed development of Web applications
* Minimalistic code
* Bindable properties and view models
* DSL for layout construction
* Debugging within browser
* Component style of development
* Simple re-use of 3rd party Javascript libraries
* Simple creation and re-use of custom components
* Built-in support for Twitter Bootstrap for a quick start

# How it works
A component is simply a method.
It is usually an external method on HTMLElement or a more specific type.  
The method takes parameters that can be either a simple value or a bindable Property.
The method usually has a final init function parameter that allows nesting content within it,
usually with a given context such as HTMLElement, HTMLSelectElement, or a custom "context" class.
The method generally adds content to the object it is invoked on.
To add simple text, it will call appendText("some text").

# How to build and run tests
On the command-line run "./gradlew build".

# Demo
See https://github.com/jean79/yested_fw/blob/master/src/jsTest/kotlin/demo.kt

## To run demo:

On the command-line run "./gradlew build".

In IntelliJ, right-click on index.html and "Open in Browser".

# Example of a Simple Component
```kotlin
fun HTMLElement.jumbotron(init: HTMLElement.()->Unit) {
    div { className = "jumbotron"
        init()
    }
}
```

# Example Usage of a Bindable Property
```kotlin
val p = Property("hello")
val validation = p.validate(errorMessage = "Name is required") { it.size > 0 }.message()
//then ui:
formGroup(state = validation) {
    btsLabel(htmlFor = "ii", width = Col.Width.Lg(4)) {
        appendText("Label")
    }
    col(Col.Width.Lg(4)) {
        textInput(value = p) //p is bindable property
    }
}
```
