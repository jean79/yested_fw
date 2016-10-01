package net.yested.core.properties

class ValidationStatus(
        val success: Boolean,
        val errorMessage: String) {
    override fun toString() = if (success) "OK" else "Error: $errorMessage"
}

fun <T> ReadOnlyProperty<T>.validate(errorMessage: String, condition: (T) -> Boolean) =
        this.map {
            if (condition(it)) {
                ValidationStatus(success = true, errorMessage = "")
            } else {
                ValidationStatus(success = false, errorMessage = errorMessage)
            }
        }

fun ReadOnlyProperty<ValidationStatus>.isValid(): Boolean {
    return get().success
}
