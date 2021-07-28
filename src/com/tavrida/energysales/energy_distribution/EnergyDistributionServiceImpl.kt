package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.dbmodel.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.dbmodel.tables.CountersTable
import com.tavrida.energysales.data_access.models.Consumer
import com.tavrida.energysales.data_access.models.Counter
import com.tavrida.energysales.data_access.models.DataContext
import com.tavrida.energysales.data_access.models.transaction
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
            val readings = CRT.select { CRT.counterId eq counterId }.map {
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

private fun MonthOfYear.prevMonth() = toLocalDate().minusMonths(1).toMonthOfYear()
private fun MonthOfYear.toLocalDate() = LocalDate.of(year, month, 1)
private fun LocalDate.toMonthOfYear() = MonthOfYear(monthValue, year)

private fun List<CounterItem>.totalConsumption(): Double {
    return sumOf { it.consumptionByMonth?.consumption ?: 0.0 }
}


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
    val orderedReadings = readings.sortedByDescending { it.readingTime }
    return CounterEnergyConsumptionByMonth(
        month = month,
        startingReading = null,
        endingReading = null,
        consumption = null
    )
    TODO()
}