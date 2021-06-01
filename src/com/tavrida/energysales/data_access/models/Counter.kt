package com.tavrida.energysales.data_access.models

import java.time.LocalDate
import java.time.LocalDateTime

data class Counter(
    val id: Int,
    val serialNumber: String,
    val consumerId: Int,
    val K: Double,
    val prevReading: PrevCounterReading,
    val readings: List<CounterReading>,
    val comment: String? = null,
    val importOrder: Int
) {
    fun lastConsumption(): Double? {
        val last = lastReading?.reading
        return if (last == null) {
            null
        } else {
            val prev = prevReading.reading
            last - prev
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
    }
}