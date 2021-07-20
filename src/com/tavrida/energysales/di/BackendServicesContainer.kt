package com.tavrida.energysales.di

import com.tavrida.energysales.data_access.DatabaseInstance
import com.tavrida.energysales.data_access.models.DataContext
import com.tavrida.energysales.energy_distribution.EnergyDistributionService
import com.tavrida.energysales.energy_distribution.EnergyDistributionServiceImpl
import com.tavrida.energysales.server.services.CounterReadingSynchronizer
import com.tavrida.energysales.server.services.CounterReadingUIController
import com.tavrida.energysales.server.settings.BackendSettings
import org.koin.core.qualifier.named
import org.koin.dsl.module


const val ApplicationCallScopeName = "ApplicationCallScope"

fun backendServicesContainer(settings: BackendSettings) = module {
    scope(named(ApplicationCallScopeName)) {
        scoped {
            DatabaseInstance.get(settings.dbDir, settings.dbName)
        }
        scoped {
            DataContext(get())
        }
        scoped { CounterReadingUIController(get()) }
        scoped { CounterReadingSynchronizer(get()) }
        scoped<EnergyDistributionService> { EnergyDistributionServiceImpl(get()) }
    }
}