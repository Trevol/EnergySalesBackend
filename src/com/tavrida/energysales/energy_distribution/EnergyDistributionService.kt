package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import kotlinx.serialization.Serializable

interface EnergyDistributionService {
    fun energyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionData
    fun monthRange(): MonthOfYearRange
    fun counterEnergyConsumptionDetails(counterId: Int): CounterEnergyConsumptionDetails
}

@Serializable
data class CounterEnergyConsumptionDetails(
    val counterId: Int,
    val readings: List<CounterReadingItem>
)

@Serializable
data class EnergyDistributionData(
    val month: MonthOfYear,
    val prevMonth: MonthOfYear,
    val total: TotalInfo,
    val counters: List<CounterItem>
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
data class CounterItem(
    val organization: OrganizationInfo,
    val id: Int,
    val sn: String,
    val K: Int,
    val comment: String?,
    val consumptionByMonth: CounterEnergyConsumptionByMonth? //в требуемый месяц данных по потреблению не найдено
)

@Serializable
data class CounterEnergyConsumptionByMonth(
    val month: MonthOfYear,
    val startingReading: CounterReadingItem?,
    val endingReading: CounterReadingItem?,
    val consumption: Double?
)

@Serializable
data class MonthOfYearRange(
    val start: MonthOfYear,
    val end: MonthOfYear,
    val lastWithReadings: MonthOfYear?
)