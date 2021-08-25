package com.tavrida.energysales.energy_distribution.v2

import com.tavrida.energysales.energy_distribution.CounterEnergyConsumptionByMonth
import com.tavrida.energysales.energy_distribution.MonthOfYear
import kotlinx.serialization.Serializable

interface EnergyDistributionServiceV2 {
    fun energyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionResult
}

@Serializable
data class EnergyDistributionResult(
    val month: MonthOfYear,
    val prevMonth: MonthOfYear,
    val toplevelUnits: List<EnergyDistributionToplevelUnit>
)

enum class EnergyDistributionItemType {
    ToplevelUnit, Organization, Counter
}

@Serializable
abstract class EnergyDistributionDataItem(val itemType: EnergyDistributionItemType)

@Serializable
data class EnergyDistributionToplevelUnit(
    val id: Int,
    val name: String,
    var total: Double?,
    val organizations: List<EnergyDistributionOrganizationItem>
) : EnergyDistributionDataItem(EnergyDistributionItemType.ToplevelUnit)

@Serializable
data class EnergyDistributionOrganizationItem(
    val id: Int, val name: String, val counters: List<EnergyDistributionCounterConsumption>
) : EnergyDistributionDataItem(EnergyDistributionItemType.Organization)

@Serializable
data class EnergyDistributionCounterConsumption(
    val id: Int,
    val sn: String,
    val K: Int,
    val comment: String?,
    val consumptionByMonth: CounterEnergyConsumptionByMonth
) : EnergyDistributionDataItem(EnergyDistributionItemType.Counter)
