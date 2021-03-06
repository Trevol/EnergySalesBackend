package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.mobile.data_contract.CounterReadingItem
import kotlinx.serialization.Serializable

interface EnergyDistributionService {
    fun energyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionData
    fun monthRange(): MonthOfYearRange
    fun counterEnergyConsumptionDetails(counterId: Int): CounterEnergyConsumptionDetails
}

@Serializable
data class CounterEnergyConsumptionDetails(
    val counterId: Int,
    val readingsWithConsumption: List<CounterReadingWithConsumption>
)

@Serializable
data class CounterReadingWithConsumption(
    val reading: CounterReadingItem,
    val readingDelta: Double?,
    val consumption: Double?,
    val continuousPowerFlow: Double? //непрерывный переток мощности
)

@Serializable
data class EnergyDistributionData(
    val month: MonthOfYear,
    val prevMonth: MonthOfYear,
    val total: TotalInfo,
    val perCounters: List<CounterInfoWithEnergyConsumption>
)

@Serializable
data class TotalInfo(val name: String, val total: Double)

@Serializable
data class OrganizationInfo(
    val id: Int,
    val name: String,
    val numberOfCounters: Int
)

@Serializable
data class CounterInfoWithEnergyConsumption(
    val organization: OrganizationInfo,
    val id: Int,
    val sn: String,
    val K: Int,
    val comment: String?,
    val consumptionByMonth: CounterEnergyConsumptionByMonth
)

@Serializable
data class CounterEnergyConsumptionByMonth(
    val month: MonthOfYear,
    val startingReading: CounterReadingItem?,
    val endingReading: CounterReadingItem?,
    val readingDelta: Double?,
    val consumption: Double?,
    val continuousPowerFlow: Double? //непрерывный переток мощности
)

@Serializable
data class MonthOfYearRange(
    val start: MonthOfYear,
    val end: MonthOfYear,
    val lastWithReadings: MonthOfYear?
)