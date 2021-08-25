package com

import database_creation.utils.println
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    fun `sum of nullable values`() {
        data class Item(val value: Double?) {
            constructor(value: Int?) : this(value?.toDouble())
        }

        val items = listOf(Item(1), Item(2), Item(3), Item(4), Item(null as Double?), Item(5))
        // val items = listOf(Item(null), Item(null), Item(null), Item(null), Item(null), Item(null))
        // val items = listOf<Item>()

        val f = items.fold(null as Double?) { acc, item ->
            if (item.value == null)
                acc
            else (acc ?: 0.0) + item.value
        }
        f.println()
    }

    @Test
    fun drop0Test() {
        listOf(1, 2, 3).drop(1) shouldBe listOf(2, 3)
    }
}
