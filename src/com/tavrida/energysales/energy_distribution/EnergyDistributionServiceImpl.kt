package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.dbmodel.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.dbmodel.tables.CountersTable
import com.tavrida.energysales.data_access.models.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import java.time.LocalDate

class EnergyDistributionServiceImpl(private val dataContext: DataContext) : EnergyDistributionService {

    override fun energyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionData {
        val monthOfYear = monthOfYear ?: recentMonth()
        val counterItems = dataContext.loadAll()
            .flatMap { org -> org.counters.map { org to it } }
            .toCounterItems(monthOfYear)
        return EnergyDistributionData(
            month = monthOfYear,
            prevMonth = monthOfYear.prevMonth(),
            total = TotalInfo("Итого", counterItems.totalConsumption()),
            counters = counterItems
        )
    }

    private fun recentMonth() = monthRange().end

    override fun monthRange(): MonthOfYearRange {
        //end: текущий месяц независимо от имеющихся данных (LocalDate.now())??
        //     или анализировать последний месяц в имеющихся показаниях???
        return MonthOfYearRange(
            start = MonthOfYear(3, 2021),
            end = MonthOfYear(7, 2021),
            lastWithReadings = null
        )
    }

    override fun counterEnergyConsumptionDetails(counterId: Int): CounterEnergyConsumptionDetails {
        return transaction(dataContext) {
            val CT = CountersTable
            CT.slice(CT.id).select { CT.id eq counterId }
                .firstOrNull() ?: throw Exception("Counter not found by id $counterId")

            val CRT = CounterReadingsTable
            val readings = CRT.select { CRT.counterId eq counterId }
                .orderBy(CRT.readingTime, SortOrder.DESC)
                .map {
                    CounterReadingItem(
                        id = it[CRT.id].value,
                        user = it[CRT.user],
                        counterId = it[CRT.counterId].value,
                        reading = it[CRT.reading],
                        readingTime = it[CRT.readingTime],
                        comment = it[CRT.comment]
                    )
                }
            CounterEnergyConsumptionDetails(
                counterId = counterId,
                readings = readings
            )
        }
    }
}

private fun List<CounterItem>.totalConsumption() = sumOf { it.consumptionByMonth?.consumption ?: 0.0 }

private fun List<Pair<Consumer, Counter>>.toCounterItems(monthOfYear: MonthOfYear): List<CounterItem> {
    return map { (organization, counter) ->
        CounterItem(
            organization = OrganizationInfo(
                id = organization.id,
                name = organization.name,
                numberOfCounters = organization.counters.size
            ),
            id = counter.id,
            sn = counter.serialNumber,
            K = counter.K.toInt(),
            comment = counter.comment,
            consumptionByMonth = counter.consumptionByMonth(monthOfYear)
        )
    }
}

private fun Counter.consumptionByMonth(month: MonthOfYear): CounterEnergyConsumptionByMonth {
    val daysDelta = 7
    /*val monthReadings = readings.byDateRange(month.extendedDateRange(daysDelta = daysDelta))
        .sortedByDescending { it.readingTime }*/

    val startingReading = readings.startingReading(month, daysDelta)?.toCounterReadingItem()
    val endingReading = readings.endingReading(month, daysDelta)?.toCounterReadingItem()

    return CounterEnergyConsumptionByMonth(
        month = month,
        startingReading = startingReading,
        endingReading = endingReading,
        consumption = consumption(startingReading, endingReading)
    )
}

private fun List<CounterReading>.startingReading(month: MonthOfYear, daysDelta: Int): CounterReading? {
    val startingReadings = byDateRange(month.firstDate().extendedDateRange(daysDelta = daysDelta))
        .sortedBy { it.readingTime }
    TODO()
}

private fun List<CounterReading>.endingReading(month: MonthOfYear, daysDelta: Int): CounterReading? {
    TODO()
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

private fun CounterReading.toCounterReadingItem() = CounterReadingItem(
    id = id,
    user = user,
    counterId = counterId,
    reading = reading,
    readingTime = readingTime,
    comment = comment
)

private fun consumption(startingReading: CounterReadingItem?, endingReading: CounterReadingItem?): Double? {
    return (endingReading ?: return null).reading - (startingReading ?: return null).reading
}