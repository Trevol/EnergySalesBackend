package com.tavrida.energysales.server.energyDistribution

import com.tavrida.energysales.data_access.models.DataContext
import com.tavrida.energysales.energy_distribution.MonthOfYear
import com.tavrida.energysales.energy_distribution.v2.EnergyDistributionServiceV2Impl
import database_creation.DbInstance
import org.junit.Test
import java.io.File

class EnergyDistributionServiceV2ImplTest {
    @Test
    fun energyDistribution_test() {
        val db = DbInstance("./databases/", "ENERGY_SALES_xls_ALL").get(recreate = false)
        val dc = DataContext(db)
        EnergyDistributionServiceV2Impl(dc).energyDistribution(MonthOfYear(6, 2021))
    }
}