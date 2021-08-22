package com.tavrida.energysales.energy_distribution.v2

import com.tavrida.energysales.data_access.models.DataContext
import com.tavrida.energysales.energy_distribution.MonthOfYear

class EnergyDistributionServiceV2Impl(private val dataContext: DataContext) : EnergyDistributionServiceV2 {
    override fun energyDistribution(monthOfYear: MonthOfYear?): EnergyDistributionResult {
        dataContext.loadAllOrganizations()
        TODO("Not yet implemented")
    }
}