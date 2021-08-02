package com.tavrida.energysales.server

import com.tavrida.energysales.energy_distribution.MonthOfYear
import database_creation.utils.println
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

class MonthOfYearTest {
    @Test
    fun compare() {
        MonthOfYear(11, 2000) shouldBeLessThanOrEqualTo MonthOfYear(10, 2021)
        MonthOfYear(11, 2000) shouldBeLessThan MonthOfYear(10, 2021)

        MonthOfYear(11, 2000) shouldBeLessThan MonthOfYear(10, 2021)

        MonthOfYear(11, 2000) shouldBe MonthOfYear(11, 2000)

        MonthOfYear(11, 2001) shouldBeGreaterThan MonthOfYear(12, 2000)
        MonthOfYear(11, 2001) shouldBeGreaterThanOrEqualTo MonthOfYear(12, 2000)

        MonthOfYear(11, 2001) shouldBeGreaterThanOrEqualTo MonthOfYear(11, 2001)
    }
}