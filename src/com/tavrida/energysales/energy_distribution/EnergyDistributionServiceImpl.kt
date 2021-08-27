package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.mobile.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.tables.CountersTable
import com.tavrida.energysales.data_access.models.*
import com.tavrida.utils.*
import org.jetbrains.exposed.sql.*
import java.time.LocalDate

class EnergyDistributionServiceImpl(private val dataContext: DataContext) : EnergyDistributionService {

    override fun energyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionData {
        val monthOfYear = monthOfYear ?: recentMonth()
        val counterItems = dataContext.selectAllOrganizations()
            .flatMap { org -> org.counters.map { org to it } }
            .toCounterItems(monthOfYear)
        return EnergyDistributionData(
            month = monthOfYear,
            prevMonth = monthOfYear.prevMonth(),
            total = TotalInfo("Итого", counterItems.totalConsumption()),
            perCounters = counterItems
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
            val K = loadCounterK(counterId)

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

            val withConsumption = readings.mapIndexed { index, currentReading ->
                // Readings sorted by readingTime DESC: prevReading for current one is at (index+1) position in readings
                val prevReading = if (index >= readings.lastIndex) null else readings[index + 1]
                CounterReadingWithConsumption(
                    reading = currentReading,
                    readingDelta = readingDelta(prevReading, currentReading),
                    consumption = consumption(prevReading, currentReading, K),
                    continuousPowerFlow = continuousPowerFlow(prevReading, currentReading, K)
                )
            }

            CounterEnergyConsumptionDetails(
                counterId = counterId,
                readingsWithConsumption = withConsumption
            )
        }
    }

    private fun loadCounterK(counterId: Int): Double {
        val CT = CountersTable
        return CT.slice(CT.K).select { CT.id eq counterId }
            .firstOrNull()
            ?.let { it[CT.K] }
            ?: throw Exception("Counter not found by id $counterId")
    }
}

private fun List<CounterInfoWithEnergyConsumption>.totalConsumption() =
    sumOf { it.consumptionByMonth?.consumption ?: 0.0 }

private fun List<Pair<Organization, Counter>>.toCounterItems(monthOfYear: MonthOfYear): List<CounterInfoWithEnergyConsumption> {
    return map { (organization, counter) ->
        CounterInfoWithEnergyConsumption(
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

