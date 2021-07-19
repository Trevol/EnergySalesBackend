package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import kotlinx.serialization.Serializable

interface EnergyDistributionService {
    fun getEnergyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionData
    fun getMonthRange(): MonthOfYearRange
}

@Serializable
data class EnergyDistributionData(
    val total: TotalInfo,
    val organizations: List<OrganizationItem>
)

@Serializable
data class TotalInfo(val name: String, val total: Double)

@Serializable
data class OrganizationItem(
    val id: Int,
    val name: String,
    val counters: List<CounterItem>
)

@Serializable
data class CounterItem(
    val id: Int,
    val sn: String,
    val K: Int,
    val comment: String,
    val consumptionByMonth: CounterEnergyConsumption? //в требуемый месяц данных по потреблению не найдено
)

@Serializable
data class CounterEnergyConsumption(
    val monthOfYear: MonthOfYear,
    val monthReading: CounterReadingItem,
    val prevMonthReading: CounterReadingItem,
    val consumption: Double
)

@Serializable
data class MonthOfYearRange(
    val start: MonthOfYear,
    val end: MonthOfYear,
    val lastWithReadings: MonthOfYear?
)

@Serializable
data class MonthOfYear(val month: Int, val year: Int) {
    operator fun compareTo(other: MonthOfYear): Int {
        val yearComparison = year.compareTo(other.year)
        if (yearComparison != 0)
            return yearComparison
        return month.compareTo(other.month)
    }
}