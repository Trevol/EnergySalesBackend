package com.tavrida.energysales.energy_distribution

interface EnergyDistributionService {
    fun getEnergyDistribution() {

    }
}

data class EnergyDistributionRow(
    val d: Int
)