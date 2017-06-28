package QUnit

/**
 * The QUnit API.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 6/16/16
 * Time: 5:48 AM
 */
val moduleStack = ArrayList<String>()

fun module(name: String, nested: () -> Unit) {
    //todo figure out why the nested function can't be passed in to QUnit natively so that nested modules show up right
    // without having to emulate it here.
    moduleStack.add(name)
    qUnitModule(moduleStack.joinToString(" "))
    nested()
    moduleStack.removeAt(moduleStack.size - 1)
    qUnitModule(moduleStack.joinToString(" "))
}

external
private fun qUnitModule(name: String) { definedExternally }

external
fun test(name: String, nested: () -> Unit) { definedExternally }

@JsName("QUnit") external val qunit2: QUnit2

external interface QUnit2 {
    val assert: Assert
}
val assert: Assert = qunit2.assert

external
interface Assert {
    fun <T> equal(actual: T, expected: T): Unit
    fun <T> notEqual(actual: T, unexpected: T): Unit
    fun <T> propEqual(actual: T, expected: T): Unit
    fun <T> notPropEqual(actual: T, unexpected: T): Unit
    fun ok(actual: Boolean): Unit
    fun notOk(actual: Boolean): Unit
    fun throws(f: () -> Any, expectedException: Throwable, message: String = definedExternally)
    fun async(): ()->Unit
}
