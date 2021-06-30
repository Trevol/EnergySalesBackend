package com.tavrida.energysales.data_access.models

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class Counter(
    val id: Int,
    val serialNumber: String,
    val consumerId: Int,
    val K: Double,
    val readings: List<CounterReading>,
    val comment: String? = null,
    val importOrder: Int
) {
    fun prevReading(forDate: LocalDate): CounterReading? {
        return readings.sortedByDescending { it.readingTime }
            .firstOrNull { forDate.daysDiff(it.readingTime.toLocalDate()) >= 7 }
    }


    fun currentConsumption(): Double? {
        val last = lastReading?.reading
        return if (last == null) {
            null
        } else {
            val prev = prevReading(LocalDate.now())?.reading ?: return null
            (last - prev) * K
        }
    }

    inline val lastReading get() = readings.maxByOrNull { it.readingTime }
    val recentReading: CounterReading?
        get() {
            val lastReading = lastReading ?: return null
            return if (lastReading.readingTime.isCurrentMonth()) {
                lastReading
            } else {
                null
            }
        }

    private companion object {
        private fun LocalDateTime.isCurrentMonth() = LocalDate.now()
            .let { now ->
                year == now.year && monthValue == now.monthValue
            }

        private fun LocalDate.daysDiff(older: LocalDate) = toEpochDay() - older.toEpochDay()
    }
}