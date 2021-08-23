package com

import database_creation.utils.println
import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.random.Random

class TestForAll {
    @Test
    fun groupByNullValueTest() {
        val recs = listOf(
            TestRec(1, 11),
            TestRec(1, 111),
            TestRec(null, -11),
            TestRec(null, -111),
        )

        val groupBy = recs.groupBy { it.group }
        groupBy.size shouldBe 2
        groupBy.get(null)!! shouldBe listOf(TestRec(null, -11), TestRec(null, -111))
        groupBy.get(1)!! shouldBe listOf(TestRec(1, 11), TestRec(1, 111))
    }

    private data class TestRec(val group: Int?, val value: Int)

    @Test
    fun appendListToList() {
        (listOf(1, 2) + listOf(4, 5)) shouldBe listOf(1, 2, 4, 5)
    }

    @Test
    fun listShouldBeSorted() {
        val l = mutableListOf<Int>()
        l.add(0, 1)
        l.add(1, 2)
        l.println()
    }

    @Test
    fun `groups of sorted list should be sorted`() {
        val rnd = Random(System.nanoTime())

        data class Item(val order: Int, val value: Int)

        fun sortedItems(): List<Item> {
            return (1..20).map { Item(it, it / 5) }
        }

        fun rndItems(): List<Item> {
            return sortedItems().sortedBy { rnd.nextInt() }
        }

        rndItems().groupBy { it.value }.forEach { (value, items) ->
            value.println()
            items.forEach { it.println() }
            "-----".println()
        }
        "===============================".println()
        sortedItems().groupBy { it.value }.forEach { (value, items) ->
            value.println()
            items.forEach { it.println() }
            "-----".println()
        }
    }
}
