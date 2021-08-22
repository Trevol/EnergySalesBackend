package com

import database_creation.utils.println
import io.kotest.matchers.collections.beSortedWith
import io.kotest.matchers.collections.shouldBeMonotonicallyIncreasing
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.LocalDateTime

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
}
