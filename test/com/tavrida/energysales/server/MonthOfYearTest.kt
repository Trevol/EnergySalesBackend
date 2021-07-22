package com.tavrida.energysales.server

import com.tavrida.energysales.energy_distribution.MonthOfYear
import database_creation.utils.println
import org.junit.Assert
import java.time.LocalDate
import kotlin.test.Test

class MonthOfYearTest {
    @Test
    fun monthIndex(){
        LocalDate.of(2020, 1, 1)
    }
    @Test
    fun compare() {
        Assert.assertTrue(MonthOfYear(11, 2000) <= MonthOfYear(10, 2021))
        Assert.assertTrue(MonthOfYear(11, 2000) < MonthOfYear(10, 2021))
        Assert.assertTrue(MonthOfYear(11, 2000) == MonthOfYear(11, 2000))
        Assert.assertTrue(MonthOfYear(11, 2001) > MonthOfYear(12, 2000))
        /*1.compareTo(1).println() // 0
        1.compareTo(0).println() // 1
        0.compareTo(1).println() // -1*/
    }
}