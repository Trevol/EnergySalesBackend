package com.tavrida.energysales.energy_distribution.v2

import com.tavrida.energysales.api.mobile.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.models.*
import com.tavrida.energysales.data_access.tables.CounterReadingsTable
import com.tavrida.energysales.energy_distribution.*
import com.tavrida.utils.orDefault
import com.tavrida.utils.orZero
import com.tavrida.utils.round3
import database_creation.utils.checkIsTrue
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
            allOrgStructureUnits = dataContext.selectOrganizationStructureUnits(),
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

    override fun counterEnergyConsumptionByMonths(counterId: Int): List<EnergyConsumptionByMonth> {
        val counter = dataContext.selectCounter(counterId)
        return counter.energyConsumptionByMonths()
    }

    override fun unitEnergyConsumptionByMonths(orgStructureUnitId: Int): List<EnergyConsumptionByMonth> {
        val organizations = dataContext.selectOrganizations(orgStructureUnitId, recursive = true)
        return organizations.energyConsumptionByMonths()
    }
}

private fun List<Organization>.energyConsumptionByMonths(): List<EnergyConsumptionByMonth> {
    val monthToConsumption = mutableMapOf<MonthOfYear, Double>()
    flatMap { it.counters }.flatMap { it.energyConsumptionByMonths() }
        .forEach { (month, consumption) ->
            monthToConsumption[month] = ((monthToConsumption[month] ?: 0.0) + consumption).round3()
        }
    return monthToConsumption.map { (month, consumption) -> EnergyConsumptionByMonth(month, consumption) }
        .sortedBy { it.month }
}


private class EnergyDistribution(
    val monthOfYear: MonthOfYear,
    val allOrgStructureUnits: List<OrganizationStructureUnit>,
    val organizations: List<Organization>
) {
    fun result(): EnergyDistributionResult {
        val toplevelUnits = calculateToplevelUnits(monthOfYear, allOrgStructureUnits, organizations)
        return EnergyDistributionResult(
            month = monthOfYear,
            prevMonth = monthOfYear.prevMonth(),
            toplevelUnits = toplevelUnits
        )
    }

    private companion object {
        fun List<OrganizationStructureUnit>.byId(orgUnitId: Int) = first { it.id == orgUnitId }
        fun List<EnergyDistributionToplevelUnit>.byId(orgUnitId: Int) = firstOrNull { it.id == orgUnitId }

        fun List<EnergyDistributionOrganizationItem>.totalConsumption() =
            flatMap { it.counters }
                .fold(null as Double?) { total, c ->
                    if (c.consumptionByMonth.consumption == null)
                        total
                    else
                        ((total ?: 0.0) + c.consumptionByMonth.consumption).round3()
                }

        fun calculateToplevelUnits(
            month: MonthOfYear,
            allOrgStructureUnits: List<OrganizationStructureUnit>,
            organizations: List<Organization>
        ): List<EnergyDistributionToplevelUnit> {
            //м.б. пока без гонки за сортировкой по counter.importOrder??
            val orgStructureToOrgs = organizations.groupBy { it.orgStructureUnitId }
            val leafUnits = orgStructureToOrgs
                .map { (orgUnitId, organizations) ->
                    val orgUnit = allOrgStructureUnits.byId(orgUnitId)
                    val orgItems = organizations.map { it.toOrgItem(month) }
                    EnergyDistributionToplevelUnit(
                        id = orgUnit.id,
                        name = orgUnit.hierarchicalName(allOrgStructureUnits),
                        total = orgItems.totalConsumption(),
                        organizations = orgItems
                    )
                }
            return calculateNonLeafNodes(leafUnits, allOrgStructureUnits)
        }

        private fun calculateNonLeafNodes(
            leafUnits: List<EnergyDistributionToplevelUnit>,
            allOrgStructureUnits: List<OrganizationStructureUnit>
        ): List<EnergyDistributionToplevelUnit> {
            val resultDataUnits = mutableListOf<EnergyDistributionToplevelUnit>()

            for (leafDataUnit in leafUnits) {
                val pathFromRoot = allOrgStructureUnits.byId(leafDataUnit.id)
                    .pathToRoot(allOrgStructureUnits)
                    .drop(1) //without leaf unit
                    .asReversed() //pathToRoot -> asReversed -> pathFromRoot

                for (pathUnit in pathFromRoot) {
                    val pathDataUnitInResult = resultDataUnits.byId(pathUnit.id)
                    if (pathDataUnitInResult == null) { //not exist in result -> simply add to result
                        //check in leafUnits - pathUnit may group organizations
                        val pathDataUnitInLeafs = leafUnits.byId(pathUnit.id)
                        if (pathDataUnitInLeafs == null) {
                            resultDataUnits.add(
                                EnergyDistributionToplevelUnit(
                                    id = pathUnit.id,
                                    name = pathUnit.hierarchicalName(allOrgStructureUnits),
                                    total = leafDataUnit.total,
                                    organizations = listOf()
                                )
                            )
                        } else {
                            // pathDataUnitInLeafs.total = pathDataUnitInLeafs.total.orZero() + leafDataUnit.total.orZero()
                            pathDataUnitInLeafs.total = sumUnitTotals(pathDataUnitInLeafs.total, leafDataUnit.total)
                            resultDataUnits.add(
                                pathDataUnitInLeafs
                            )
                        }
                    } else {
                        // pathDataUnitInResult.total = pathDataUnitInResult.total.orZero() + leafDataUnit.total.orZero()
                        pathDataUnitInResult.total = sumUnitTotals(pathDataUnitInResult.total, leafDataUnit.total)
                    }
                }
                resultDataUnits.add(leafDataUnit)
            }

            return resultDataUnits
        }

        private fun sumUnitTotals(pathUnitTotal: Double?, leafUnitTotal: Double?) =
            if (pathUnitTotal == null && leafUnitTotal == null) null
            else (pathUnitTotal.orZero() + leafUnitTotal.orZero()).round3()

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
