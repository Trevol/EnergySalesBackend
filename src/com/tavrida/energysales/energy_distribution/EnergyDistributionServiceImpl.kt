package com.tavrida.energysales.energy_distribution

import org.jetbrains.exposed.sql.Database

class EnergyDistributionServiceImpl(val db: Database) : EnergyDistributionService {
    override fun getEnergyDistribution(monthOfYear: MonthOfYear?): List<EnergyDistributionRow> {
        val monthOfYear = monthOfYear ?: recentMonth()
        TODO("Not yet implemented")
    }

    private fun recentMonth(): MonthOfYear {
        //текущий месяц независимо от имеющихся данных (LocalDate.now())??
        //или анализировать последний месяц в имеющихся показаниях???
        TODO("Not yet implemented")
    }

    override fun getMonthRange(): MonthOfYearRange {
        TODO("Not yet implemented")
    }
}