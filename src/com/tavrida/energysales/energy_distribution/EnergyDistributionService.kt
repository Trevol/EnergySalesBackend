package com.tavrida.energysales.energy_distribution

import kotlinx.serialization.Serializable

interface EnergyDistributionService {
    fun getEnergyDistribution(monthOfYear: MonthOfYear?): List<EnergyDistributionRow>
    fun getMonthRange(): MonthOfYearRange
}

@Serializable
data class MonthOfYearRange(
    val start: MonthOfYear,
    val end: MonthOfYear,
    val lastWithReadings: MonthOfYear?
)

@Serializable
data class EnergyDistributionRow(
    val organizationId: Int,
    val organizationName: String,
    val counterId: Int,
    val counterSN: String
    //And so on
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