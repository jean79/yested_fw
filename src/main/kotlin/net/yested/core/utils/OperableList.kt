package net.yested.core.utils

/**
 * A list that can be operated upon to clarify what kinds of animations should happen when updating it in a UI.
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 4/4/2017
 * Time: 11:49 PM
 */
interface OperableList<T> {
    fun size(): Int
    fun get(index: Int): T
    fun add(index: Int, item: T)
    fun removeAt(index: Int): T
    fun move(fromIndex: Int, toIndex: Int)

    fun indexOf(item: T): Int {
       var index = size() - 1
       while (index >= 0 && get(index) != item) {
           index--
       }
       return index
    }

    fun contains(item: T): Boolean = indexOf(item) >= 0
}

fun <T> OperableList<T>.toList(): List<T> = range().map { get(it) }

fun <T> OperableList<T>.range() = (0..(size() - 1))

fun <T> OperableList<T>.reconcileTo(desiredList: List<T>) {
    // delete anything that isn't in desiredList
    range().reversed().forEach { if (!desiredList.contains(get(it))) removeAt(it) }
    val (desiredListWithoutNew, newItems) = desiredList.partition { contains(it) }

    var countMovingRight = 0
    var countMovingLeft = 0
    range().forEach { index ->
        val desiredIndex = desiredListWithoutNew.indexOf(get(index))
        if (desiredIndex > index) {
            countMovingRight++
        } else if (desiredIndex < index) {
            countMovingLeft++
        }
    }
    val desiredIndices = if (countMovingLeft <= countMovingRight) {
        0..(desiredListWithoutNew.size - 1)
    } else {
        (0..(desiredListWithoutNew.size - 1)).reversed()
    }
    desiredIndices.forEach { desiredIndex ->
        val desiredItem = desiredListWithoutNew[desiredIndex]
        val indexToMove = indexOf(desiredItem)
        if (indexToMove != desiredIndex) {
            move(indexToMove, desiredIndex)
        }
    }
    for (newItem in newItems) {
        add(desiredList.indexOf(newItem), newItem)
    }
}

class InMemoryOperableList<T>(val list: MutableList<T>) : OperableList<T> {
    var modificationCount = 0

    override fun size(): Int = list.size

    override fun get(index: Int): T = list[index]

    override fun add(index: Int, item: T) {
        modificationCount++
        list.add(index, item)
    }

    override fun removeAt(index: Int): T {
        modificationCount++
        return list.removeAt(index)
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        modificationCount++
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
    }
}
