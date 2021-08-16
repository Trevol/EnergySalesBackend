package com.tavrida.energysales.data_access.models

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.abs


private const val recencyDaysThreshold = 7

data class Counter(
    val id: Int,
    val serialNumber: String,
    val organizationId: Int,
    val K: Double,
    val readings: MutableList<CounterReading>,
    val comment: String? = null,
    val importOrder: Int
) {
    fun prevReading(forDate: LocalDate): CounterReading? {
        return readings.sortedByDescending { it.readingTime }
            .firstOrNull { forDate.daysDiff(it.readingTime.toLocalDate()) >= recencyDaysThreshold }
    }


    fun currentConsumption(): Double? {
        val recent = recentReading?.reading
        return if (recent == null) {
            null
        } else {
            val prev = prevReading(LocalDate.now())?.reading ?: return null
            (recent - prev) * K
        }
    }

    inline val lastReading get() = readings.maxByOrNull { it.readingTime }

    val recentReading: CounterReading?
        get() {
            val lastReading = lastReading ?: return null
            val isRecent = abs(lastReading.readingTime.toLocalDate().daysDiff(LocalDate.now())) < recencyDaysThreshold
            return if (isRecent) lastReading else null
        }

    private companion object {
        private fun LocalDateTime.isCurrentMonth() = LocalDate.now()
            .let { now ->
                year == now.year && monthValue == now.monthValue
            }

        private fun LocalDate.daysDiff(older: LocalDate) = toEpochDay() - older.toEpochDay()
    }
}