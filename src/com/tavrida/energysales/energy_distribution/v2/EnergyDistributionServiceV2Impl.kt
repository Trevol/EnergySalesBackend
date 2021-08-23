package com.tavrida.energysales.energy_distribution.v2

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.models.DataContext
import com.tavrida.energysales.energy_distribution.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EnergyDistributionServiceV2Impl(private val dataContext: DataContext) : EnergyDistributionServiceV2 {

    override fun energyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionResult {
        dataContext.selectAllOrganizations()
        dataContext.selectOrganizationStructureUnits()

        // TODO("Not yet implemented")
        return Synthetic.energyDistributionResult(monthOfYear)
    }

}

private object Synthetic {
    fun energyDistributionResult(monthOfYear: MonthOfYear?): EnergyDistributionResult {
        val month = monthOfYear ?: LocalDate.now().toMonthOfYear()
        val toplevelUnits = listOf(
            energyDistributionToplevelUnit(1, month, numOfCounters = 1),
            energyDistributionToplevelUnit(2, month, numOfCounters = 2)
        )
        return EnergyDistributionResult(month, month.prevMonth(), toplevelUnits)
    }

    private fun energyDistributionToplevelUnit(
        i: Int,
        month: MonthOfYear,
        numOfCounters: Int
    ): EnergyDistributionToplevelUnit {
        return EnergyDistributionToplevelUnit(
            name = "Top level $i",
            total = 12345.68,
            organizations = listOf(
                EnergyDistributionOrganizationItem(
                    id = i,
                    name = "Organization_$i",
                    counters = (1..numOfCounters).map { nOfCounter ->
                        EnergyDistributionCounterConsumption(
                            id = i * 10 + nOfCounter,
                            sn = "$i$i$i$i$i",
                            K = 1,
                            consumptionByMonth = CounterEnergyConsumptionByMonth(
                                month = month,
                                startingReading = CounterReadingItem(
                                    id = i * 10 + nOfCounter,
                                    user = "Sasha",
                                    counterId = i,
                                    reading = 1123.0,
                                    readingTime = LocalDateTime.of(month.firstDate(), LocalTime.of(12, 33, 45)),
                                    comment = "This is reading of counter $i"
                                ),
                                endingReading = CounterReadingItem(
                                    id = 2,
                                    user = "Sasha",
                                    counterId = i,
                                    reading = 2123.0,
                                    readingTime = LocalDateTime.of(month.lastDate(), LocalTime.of(11, 32, 44)),
                                    comment = "This is reading of counter $i"
                                ),
                                readingDelta = 1000.0,
                                consumption = 1000.0,
                                continuousPowerFlow = 0.067
                            ),
                            comment = "This is counter $i"
                        )

                    }
                )
            )
        )
        TODO()
    }
}
