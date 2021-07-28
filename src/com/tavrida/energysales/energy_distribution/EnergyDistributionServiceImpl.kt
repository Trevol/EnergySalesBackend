package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.dbmodel.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.dbmodel.tables.CountersTable
import com.tavrida.energysales.data_access.models.*
import com.tavrida.utils.round3
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
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
        return transaction(dataContext) {
            val CRT = CounterReadingsTable
            val (start, end) = CRT.slice(CRT.readingTime.min(), CRT.readingTime.max())
                .selectAll().first()
                .let {
                    it[CRT.readingTime.min()]?.toLocalDate() to it[CRT.readingTime.max()]?.toLocalDate()
                }
            val now = LocalDate.now()
            MonthOfYearRange(
                start = (start ?: now).toMonthOfYear(),
                end = (end ?: now).toMonthOfYear(),
                lastWithReadings = null
            )
        }
    }

    override fun counterEnergyConsumptionDetails(counterId: Int): CounterEnergyConsumptionDetails {
        return transaction(dataContext) {
            checkCounter(counterId)

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

    private fun checkCounter(counterId: Int) {
        val CT = CountersTable
        CT.slice(CT.id).select { CT.id eq counterId }
            .firstOrNull() ?: throw Exception("Counter not found by id $counterId")
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

private fun CounterReading.toCounterReadingItem() = CounterReadingItem(
    id = id,
    user = user,
    counterId = counterId,
    reading = reading,
    readingTime = readingTime,
    comment = comment
)

private fun consumption(startingReading: CounterReadingItem?, endingReading: CounterReadingItem?): Double? {
    return ((endingReading ?: return null).reading - (startingReading ?: return null).reading).round3()
}