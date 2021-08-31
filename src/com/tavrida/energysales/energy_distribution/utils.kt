package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.mobile.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.models.Counter
import com.tavrida.energysales.data_access.models.CounterReading
import com.tavrida.utils.div
import com.tavrida.utils.round3
import com.tavrida.utils.times
import database_creation.utils.checkIsTrue
import java.lang.Exception
import java.time.LocalDate


fun Counter.consumptionByMonth(month: MonthOfYear, daysDelta: Int = 7): CounterEnergyConsumptionByMonth {
    val startingReading = readings.startingReading(month, daysDelta)?.toCounterReadingItem()
    val endingReading = readings.endingReading(month, daysDelta)?.toCounterReadingItem()

    return CounterEnergyConsumptionByMonth(
        month = month,
        startingReading = startingReading,
        endingReading = endingReading,
        readingDelta = readingDelta(startingReading, endingReading),
        consumption = consumption(startingReading, endingReading, K),
        continuousPowerFlow = continuousPowerFlow(startingReading, endingReading, K)
    )
}

private fun readingDelta(starting: Double, ending: Double) = (ending - starting).round3()

fun readingDelta(startingReading: CounterReadingItem?, endingReading: CounterReadingItem?): Double? {
    if (endingReading == null || startingReading == null) {
        return null
    }
    if (endingReading.reading < startingReading.reading) {
        return readingDeltaWithCycle(startingReading.reading, endingReading.reading)
    }
    return readingDelta(startingReading.reading, endingReading.reading)
}

fun readingDelta(startingReading: CounterReading?, endingReading: CounterReading?): Double? {
    if (endingReading == null || startingReading == null) {
        return null
    }
    if (endingReading.reading < startingReading.reading) {
        return readingDeltaWithCycle(startingReading.reading, endingReading.reading)
    }
    return readingDelta(startingReading.reading, endingReading.reading)
}

fun readingDeltaWithCycle(startingReading: Double, endingReading: Double): Double {
    checkIsTrue(startingReading > endingReading)
    val startingIntPart = startingReading.toLong().toString()
    val endingIntPart = endingReading.toLong().toString()

    checkIsTrue(startingIntPart[0] == '9')
    checkIsTrue(startingIntPart.length > endingIntPart.length)

    // 97210 - start
    //  1204 - end
    //     1 - length diff
    //    10 - should prefix with 10 10~1
    //101204
    val prefix = power10(startingIntPart.length - endingIntPart.length).toString()

    val correctedEndingReading = (prefix + endingReading.toString()).toDouble()
    return readingDelta(startingReading, correctedEndingReading)
}

object power10 {
    private val powersOf10 = arrayOf(
        1, 10, 100, 1000, 10_000, 100_000,
        1000_000, 10_000_000, 100_000_000,
        1000_000_000, 10_000_000_000
    )

    operator fun invoke(power: Int): Long {
        return powersOf10[power]
        /*checkIsTrue(pow > 0)
        var result = 10L
        for (i in 1 until pow) {
            result *= 10
        }
        return result*/
    }
}

fun consumption(
    startingReading: CounterReadingItem?,
    endingReading: CounterReadingItem?,
    K: Double
) = (readingDelta(startingReading, endingReading) * K)?.round3()

fun consumption(
    startingReading: CounterReading?,
    endingReading: CounterReading?,
    K: Double
) = (readingDelta(startingReading, endingReading) * K)?.round3()

fun continuousPowerFlow(
    startingReading: CounterReadingItem?,
    endingReading: CounterReadingItem?,
    K: Double
): Double? {
    val kwh = consumption(startingReading, endingReading, K) // energy consumption in kilowatt-hours
    val durationHours = durationHours(startingReading, endingReading)
    return (kwh / durationHours)?.round3()
}

private inline fun durationHours(startingReading: CounterReadingItem?, endingReading: CounterReadingItem?) =
    com.tavrida.utils.durationHours(startingReading?.readingTime, endingReading?.readingTime)

private fun CounterReading.toCounterReadingItem() = CounterReadingItem(
    id = id,
    user = user,
    counterId = counterId,
    reading = reading,
    readingTime = readingTime,
    comment = comment
)

private fun List<CounterReading>.startingReading(month: MonthOfYear, daysDelta: Int): CounterReading? {
    val startingReadings = month.firstDate().extendedDateRange(daysDelta = daysDelta)
        .let { byDateRange(it) }
        .sortedBy { it.readingTime }
    return startingReadings.lastOrNull()
}

private fun List<CounterReading>.endingReading(month: MonthOfYear, daysDelta: Int): CounterReading? {
    val endingReadings = month.lastDate().extendedDateRange(daysDelta = daysDelta)
        .let { byDateRange(it) }
        .sortedBy { it.readingTime }
    return endingReadings.lastOrNull()
}


private fun List<CounterReading>.byDateRange(range: DateRange) = filter {
    it.readingTime.toLocalDate()
        .let { d ->
            range.start <= d && d <= range.end
        }
}

private fun MonthOfYear.extendedDateRange(daysDelta: Int) = DateRange(
    start = firstDate().minusDays(daysDelta.toLong()),
    end = lastDate().plusDays(daysDelta.toLong())
)

private fun LocalDate.extendedDateRange(daysDelta: Int) = DateRange(
    start = minusDays(daysDelta.toLong()),
    end = plusDays(daysDelta.toLong())
)

private data class DateRange(val start: LocalDate, val end: LocalDate)

