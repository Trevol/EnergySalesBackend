package database_creation.xlsx

import com.tavrida.energysales.data_access.models.Organization
import database_creation.xlsx.reader.OrganizationsWithStructureXlsReader

object OrganizationChecker {
    fun List<Organization>.check() {
        checkNameDuplicates()
        checkSerialNumberDuplicates()
    }

    private fun List<Organization>.checkNameDuplicates() {
        val duplicatedNames = groupBy { it.name }
            .filter { it.value.size > 1 }
            .map { it.key }
        if (duplicatedNames.isNotEmpty()) {
            throw Exception("Organizations with duplicated names found: $duplicatedNames")
        }
    }

    private fun List<Organization>.checkSerialNumberDuplicates() {
        val duplicates = flatMap { org -> org.counters.map { org to it } }
            .groupBy { (org, counter) -> counter.serialNumber }
            .filter { it.value.size > 1 }
            .map { it.value.map { (org, counter) -> org.name to counter.serialNumber } }
        if (duplicates.isNotEmpty()) {
            throw Exception("Duplicated serial numbers found: $duplicates")
        }
    }

    @JvmName("checkSerialNumberDuplicatesOnListOfRecords")
    fun List<OrganizationsWithStructureXlsReader.OrganizationCounterReadingRecord>.checkSerialNumberDuplicates(){
        val duplicates = groupBy { it.serialNumber }
            .filter { it.value.size > 1 }
            .map { it.key }
        if (duplicates.isNotEmpty()) {
            throw Exception("Organizations with duplicated names found: $duplicates")
        }
    }
}