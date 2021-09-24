package com

import com.tavrida.energysales.energy_distribution.MonthOfYear
import com.tavrida.energysales.energy_distribution.readingDeltaWithCycle
import com.tavrida.utils.round3
import database_creation.utils.checkIsTrue
import database_creation.utils.println
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class TestForAll {

    @Test
    fun mapByMonthOfYear() {
        val m = mutableMapOf(MonthOfYear(1, 2121) to 123.456)
        m[MonthOfYear(4, 2121)] = 123.45
        m.println()
        m[MonthOfYear(4, 2121)].println()
    }

    @Test
    fun mapPlusMap() {
        val map1 = mapOf(1 to 11, 2 to 22)
        val map2 = mapOf(4 to 44, 3 to 33)

        (map1 + map2) shouldBe mapOf(1 to 11, 2 to 22, 4 to 44, 3 to 33)
        (map1 + map2).println()
    }

    @Test
    fun parseInstant() {
        // val dt = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss").parse("20.09.21 20:00:00")
        val temporal = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss:SSS").parse("20.09.21 20:00:00:002")
        temporal.println()
        val dt = LocalDateTime.from(temporal).toInstant(ZoneOffset.UTC)
        Instant.from(dt).println()
    }

    @Test
    fun formatDateTime(){
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")
        val formatted = formatter.format(LocalDateTime.now())
        formatted.println()
        formatter.parse(formatted)
            .println()
    }
}
