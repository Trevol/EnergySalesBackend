package com

import com.tavrida.energysales.energy_distribution.MonthOfYear
import database_creation.utils.println
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.random.Random

class TestForAll {
    @Test
    fun mapByMonthOfYear() {
        val m = mutableMapOf(MonthOfYear(1, 2121) to 123.456)
        m[MonthOfYear(4, 2121)] = 123.45
        m.println()
        m[MonthOfYear(4, 2121)].println()
    }

}
