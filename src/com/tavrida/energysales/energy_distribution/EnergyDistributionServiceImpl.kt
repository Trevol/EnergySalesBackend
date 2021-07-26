package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.models.Consumer
import com.tavrida.energysales.data_access.models.Counter
import com.tavrida.energysales.data_access.models.DataContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.round

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
            consumptionByMonth = CounterEnergyConsumption(
                monthReading = CounterReadingItem(
                    id = 1,
                    user = "Sasha",
                    counterId = counter.id,
                    reading = 1111.0,
                    readingTime = LocalDateTime.of(monthOfYear.toLocalDate(), LocalTime.of(10, 31)),
                    comment = null
                ),
                prevMonthReading = CounterReadingItem(
                    id = 1,
                    user = "Sasha",
                    counterId = counter.id,
                    reading = 3333.0,
                    readingTime = LocalDateTime.of(monthOfYear.prevMonth().toLocalDate(), LocalTime.of(11, 20)),
                    comment = null
                ),
                consumption = 2222.0
            )
        )
    }
}

private fun Counter.consumptionByMonth(month: MonthOfYear): CounterEnergyConsumption {
    val monthReading = CounterReadingItem(
        id = 1,
        user = "Sasha",
        counterId = id,
        reading = 1111.0,
        readingTime = LocalDateTime.of(month.toLocalDate(), LocalTime.of(10, 31)),
        comment = null
    )
    val prevMonthReading = CounterReadingItem(
        id = 1,
        user = "Sasha",
        counterId = id,
        reading = 3333.0,
        readingTime = LocalDateTime.of(month.prevMonth().toLocalDate(), LocalTime.of(11, 20)),
        comment = null
    )
    return CounterEnergyConsumption(
        monthReading = monthReading,
        prevMonthReading = prevMonthReading,
        consumption = 2222.0
    )

}