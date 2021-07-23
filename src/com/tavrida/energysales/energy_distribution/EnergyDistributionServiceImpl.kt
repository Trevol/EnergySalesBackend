package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.models.Consumer
import com.tavrida.energysales.data_access.models.Counter
import com.tavrida.energysales.data_access.models.DataContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EnergyDistributionServiceImpl(private val dataContext: DataContext) : EnergyDistributionService {

    override fun energyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionData {
        val monthOfYear = monthOfYear ?: recentMonth()
        val counterItems = dataContext.loadAll().flatMap { it.counters }.toCounterItems(monthOfYear)
        return EnergyDistributionData(
            month = monthOfYear,
            prevMonth = monthOfYear.prevMonth(),
            total = TotalInfo("Итого", counterItems.totalConsumption()),
            counters = counterItems
        )
    }

    private fun recentMonth(): MonthOfYear {
        return MonthOfYear(7, 2021)
        //текущий месяц независимо от имеющихся данных (LocalDate.now())??
        //или анализировать последний месяц в имеющихся показаниях???
        // TODO("Not yet implemented")
    }

    override fun monthRange(): MonthOfYearRange {
        return MonthOfYearRange(
            start = MonthOfYear(3, 2021),
            end = MonthOfYear(7, 2021),
            lastWithReadings = null
        )
        // TODO("Not yet implemented")
    }
}

private fun MonthOfYear.prevMonth() = toLocalDate().minusMonths(1).toMonthOfYear()
private fun MonthOfYear.toLocalDate() = LocalDate.of(year, month, 1)
private fun LocalDate.toMonthOfYear() = MonthOfYear(monthValue, year)

private fun List<CounterItem>.totalConsumption(): Double {
    return sumOf { it.consumptionByMonth?.consumption ?: 0.0 }
}

private fun List<Counter>.toCounterItems(monthOfYear: MonthOfYear): List<CounterItem> {
    val nOfOrganizations = 3
    val nOfCounters = 2
    return (1..nOfOrganizations).map { orgId ->
        val org = OrganizationInfo(
            id = orgId,
            name = "Organization_$orgId",
            numberOfCounters = nOfCounters
        )
        (1..nOfCounters).map { counterPos ->
            val sn = "000$orgId$counterPos"
            val counterId = "$orgId$counterPos".toInt()
            CounterItem(
                organization = org,
                id = counterId,
                sn = sn,
                K = 1,
                comment = "Counter $sn",
                consumptionByMonth = CounterEnergyConsumption(
                    monthReading = CounterReadingItem(
                        id = 1,
                        user = "Sasha",
                        counterId = counterId,
                        reading = 1111.0,
                        readingTime = LocalDateTime.of(monthOfYear.toLocalDate(), LocalTime.of(10, 31)),
                        comment = null
                    ),
                    prevMonthReading = CounterReadingItem(
                        id = 1,
                        user = "Sasha",
                        counterId = counterId,
                        reading = 3333.0,
                        readingTime = LocalDateTime.of(monthOfYear.prevMonth().toLocalDate(), LocalTime.of(11, 20)),
                        comment = null
                    ),
                    consumption = 2222.0
                )
            )
        }
    }.flatMap { it }
}
