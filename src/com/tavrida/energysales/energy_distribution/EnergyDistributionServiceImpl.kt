package com.tavrida.energysales.energy_distribution

import com.tavrida.energysales.data_access.models.Consumer
import com.tavrida.energysales.data_access.models.DataContext

class EnergyDistributionServiceImpl(private val dataContext: DataContext) : EnergyDistributionService {
    override fun getEnergyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionData {
        val monthOfYear = monthOfYear ?: recentMonth()
        val organizations = dataContext.loadAll().toOrganizationItems(monthOfYear)
        return EnergyDistributionData(
            total = TotalInfo("Итого", organizations.totalConsumption()),
            organizations = organizations
        )
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

private fun List<OrganizationItem>.totalConsumption(): Double {
    return flatMap { it.counters }.sumOf { it.consumptionByMonth?.consumption ?: 0.0 }
}

private fun List<Consumer>.toOrganizationItems(monthOfYear: MonthOfYear): List<OrganizationItem> {
    TODO("Not yet implemented")
}
