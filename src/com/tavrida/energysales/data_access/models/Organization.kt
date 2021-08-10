package com.tavrida.energysales.data_access.models

data class Organization(
    val id: Int,
    val orgStructureUnitId: Int,
    var orgStructureUnit: OrganizationStructureUnit?,
    val name: String,
    var counters: MutableList<Counter> = mutableListOf(),
    val comment: String? = null,
    val importOrder: Int
) {
    val countersInfo =
        counters.sortedBy { it.serialNumber }.map { "${it.serialNumber}(${it.importOrder})" }
            .joinToString(separator = ", ")

    fun allCountersHaveRecentReadings() = counters.all { it.recentReading != null }
    fun allCountersAreSynchronized() =
        counters.all { it.readings.all { it.synchronized } }
}

