package com

import database_creation.utils.println
import io.kotest.matchers.collections.beSortedWith
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.Test

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
        (listOf(1, 2, 3) == listOf(1, 2, 3)).println()
        (listOf(1, 2, 3) == listOf(1, 2, 3, 4)).println()
        (listOf(1, 2, 3) == listOf(1, 3, 2)).println()
    }
}