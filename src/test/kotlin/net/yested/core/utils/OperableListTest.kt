package net.yested.core.utils

import org.junit.Test
import spec.mustBe

/**
 * A test for [OperableList].
 * @author Eric Pabst (epabst@gmail.com)
 * Date: 9/29/16
 * Time: 1:51 PM
 */
class OperableListTest {

    @Test
    fun shouldDoNothingForTwoIdenticalLists() {
        val originalList = InMemoryOperableList(mutableListOf(1, 2, 3))
        originalList.reconcileToAndVerify(listOf(1, 2, 3))
        originalList.modificationCount.mustBe(0)
    }

    @Test
    fun shouldAddToEndOfShortList() {
        val originalList = InMemoryOperableList(mutableListOf(1))
        originalList.reconcileToAndVerify(listOf(1, 2))
        originalList.modificationCount.mustBe(1)
    }

    @Test
    fun shouldAddToEndOfShortList2() {
        val originalList = InMemoryOperableList(mutableListOf(1, 2))
        originalList.reconcileToAndVerify(listOf(1, 2, 3))
        originalList.modificationCount.mustBe(1)
    }

    @Test
    fun shouldMoveSingleItemToEnd() {
        val originalList = InMemoryOperableList(mutableListOf(1, 6, 2, 3, 4, 5))
        originalList.reconcileToAndVerify(listOf(1, 2, 3, 4, 5, 6))
        originalList.modificationCount.mustBe(1)
    }

    @Test
    fun shouldMoveSingleItemToBeginning() {
        val originalList = InMemoryOperableList(mutableListOf(2, 3, 4, 5, 6, 1))
        originalList.reconcileToAndVerify(listOf(1, 2, 3, 4, 5, 6))
        originalList.modificationCount.mustBe(1)
    }

    @Test
    fun shouldMoveSingleItemEarlier() {
        val originalList = InMemoryOperableList(mutableListOf(1, 2, 3, 5, 6, 4, 7))
        originalList.reconcileToAndVerify(listOf(1, 2, 3, 4, 5, 6, 7))
        originalList.modificationCount.mustBe(1)
    }

    @Test
    fun shouldMoveTwoItemsEarlier() {
        val originalList = InMemoryOperableList(mutableListOf(9, 6, 5, 4, 3, 8, 7, 2, 1))
        originalList.reconcileToAndVerify(listOf(9, 8, 7, 6, 5, 4, 3, 2, 1))
        originalList.modificationCount.mustBe(2)
    }

    @Test
    fun shouldMoveTwoItemsEarlierSwappingTheirOrder() {
        val originalList = InMemoryOperableList(mutableListOf(9, 6, 5, 4, 3, 7, 8, 2, 1))
        originalList.reconcileToAndVerify(listOf(9, 8, 7, 6, 5, 4, 3, 2, 1))
        originalList.modificationCount.mustBe(2)
    }

    @Test
    fun shouldMoveTwoItemsLater() {
        val originalList = InMemoryOperableList(mutableListOf(3, 2, 9, 8, 7, 6, 5, 4, 1))
        originalList.reconcileToAndVerify(listOf(9, 8, 7, 6, 5, 4, 3, 2, 1))
        originalList.modificationCount.mustBe(2)
    }

    @Test
    fun shouldDeleteOneItem() {
        val originalList = InMemoryOperableList(mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1))
        originalList.reconcileToAndVerify(listOf(9, 8, 7, 5, 4, 3, 2, 1))
        originalList.modificationCount.mustBe(1)
    }

    @Test
    fun shouldDeleteTwoItems() {
        val originalList = InMemoryOperableList(mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1))
        originalList.reconcileToAndVerify(listOf(9, 8, 7, 5, 4, 2, 1))
        originalList.modificationCount.mustBe(2)
    }

    @Test
    fun shouldAddTwoItems() {
        val originalList = InMemoryOperableList(mutableListOf(9, 7, 6, 5, 4, 3, 1))
        originalList.reconcileToAndVerify(listOf(9, 8, 7, 6, 5, 4, 3, 2, 1))
        originalList.modificationCount.mustBe(2)
    }

    @Test
    fun shouldAddTwoItemsNearEnd_traversingFromRight() {
        val originalList = InMemoryOperableList(mutableListOf(9, 8, 7, 6, 5, 1))
        originalList.reconcileToAndVerify(listOf(8, 7, 6, 5, 9, 4, 3, 2, 1))
        originalList.modificationCount.mustBe(4)
    }

    @Test
    fun shouldHandleReversal() {
        val originalList = InMemoryOperableList((1..10).reversed().toMutableList())
        originalList.reconcileToAndVerify((1..10).toList())
        originalList.modificationCount.mustBe(9)
    }

    @Test
    fun shouldHandleTotalReplacement() {
        val originalList = InMemoryOperableList((1..10).toMutableList())
        originalList.reconcileToAndVerify((11..20).toList())
        originalList.modificationCount.mustBe(20)
    }
}

private fun <T> OperableList<T>.reconcileToAndVerify(desiredList: List<T>) {
    reconcileTo(desiredList)
    toList().mustBe(desiredList)
}
