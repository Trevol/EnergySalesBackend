package com.tavrida.energysales.energy_distribution.v2

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.models.DataContext
import com.tavrida.energysales.data_access.models.Organization
import com.tavrida.energysales.data_access.models.OrganizationStructureUnit
import com.tavrida.energysales.data_access.models.transaction
import com.tavrida.energysales.data_access.tables.CounterReadingsTable
import com.tavrida.energysales.energy_distribution.*
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.min
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EnergyDistributionServiceV2Impl(private val dataContext: DataContext) : EnergyDistributionServiceV2 {

    override fun energyDistribution(monthOfYear: MonthOfYear?) =
        EnergyDistribution(
            monthOfYear = monthOfYear ?: recentMonth(),
            orgStructureUnits = dataContext.selectOrganizationStructureUnits(),
            organizations = dataContext.selectAllOrganizations()
        ).result()

    private fun recentMonth() = monthRange().end

    private fun monthRange(): MonthOfYearRange {
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

}

private class EnergyDistribution(
    val monthOfYear: MonthOfYear,
    val orgStructureUnits: List<OrganizationStructureUnit>,
    val organizations: List<Organization>
) {
    fun result(): EnergyDistributionResult {
        val toplevelUnits = calculateToplevelUnits(monthOfYear, orgStructureUnits, organizations)
        return EnergyDistributionResult(
            month = monthOfYear,
            prevMonth = monthOfYear.prevMonth(),
            toplevelUnits = toplevelUnits
        )
    }

    private companion object {
        fun List<OrganizationStructureUnit>.byId(orgUnitId: Int) = first { it.id == orgUnitId }

        fun List<EnergyDistributionOrganizationItem>.totalConsumption() =
            flatMap { it.counters }
                .fold(null as Double?) { total, c ->
                    if (c.consumptionByMonth.consumption == null)
                        total
                    else
                        (total ?: 0.0) + c.consumptionByMonth.consumption
                }

        fun calculateToplevelUnits(
            month: MonthOfYear,
            orgStructureUnits: List<OrganizationStructureUnit>,
            organizations: List<Organization>
        ): List<EnergyDistributionToplevelUnit> {
            //м.б. пока без гонки за сортировкой по counter.importOrder??
            val orgStructureToOrgs = organizations.groupBy { it.orgStructureUnitId }
            val toplevelUnits = orgStructureToOrgs
                .map { (orgUnitId, organizations) ->
                    val orgUnit = orgStructureUnits.byId(orgUnitId)
                    val orgItems = organizations.map { it.toOrgItem(month) }
                    EnergyDistributionToplevelUnit(
                        id = orgUnit.id,
                        name = orgUnit.hierarchicalName(orgStructureUnits),
                        total = orgItems.totalConsumption(),
                        organizations = orgItems
                    )
                }
            // TODO: include non leaf nodes (root and intermediate) with total aggregation
            return toplevelUnits
        }

        private fun OrganizationStructureUnit.hierarchicalName(allUnits: List<OrganizationStructureUnit>) =
            pathFromRoot(allUnits).joinToString(" / ") { it.name }

        private fun OrganizationStructureUnit.pathToRoot(allUnits: List<OrganizationStructureUnit>): Iterable<OrganizationStructureUnit> {
            return sequence {
                var current = this@pathToRoot
                yield(current)
                while (current.parentId != null) {
                    current = allUnits.byId(current.parentId!!)
                    yield(current)
                }
            }.asIterable()
        }

        private fun OrganizationStructureUnit.pathFromRoot(allUnits: List<OrganizationStructureUnit>) =
            pathToRoot(allUnits).reversed()


        private fun Organization.toOrgItem(month: MonthOfYear): EnergyDistributionOrganizationItem {
            val it = this
            return EnergyDistributionOrganizationItem(
                id = it.id,
                name = it.name,
                counters = it.counters.map {
                    EnergyDistributionCounterConsumption(
                        id = it.id,
                        sn = it.serialNumber,
                        K = it.K.toInt(),
                        comment = it.comment,
                        consumptionByMonth = it.consumptionByMonth(month)
                    )
                }
            )
        }
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
            id = i,
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
