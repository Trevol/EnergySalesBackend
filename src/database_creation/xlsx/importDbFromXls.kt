package database_creation.xlsx

import com.tavrida.energysales.data_access.models.*
import database_creation.utils.dataContextWithTimestampedDb
import database_creation.utils.log
import database_creation.utils.println
import database_creation.xlsx.reader.OrganizationsXlsReader
import java.io.File
import java.time.LocalDateTime

fun main() {
    // TODO()
    // июнь
    val prevReadingTime: LocalDateTime = LocalDateTime.of(2021, 6, 1, 10, 30, 30) //01.06.2021 10:30:30
    val currentReadingTime: LocalDateTime = LocalDateTime.of(2021, 7, 1, 10, 30, 30) //01.07.2021 10:30:30

    val organizations = "./databases/xlsx/import 2021.06 июнь/import 2021.06 июнь.xlsx"
        .let { File(it) }
        .let { OrganizationsXlsReader.read(it) }
        .toOrganizations(prevReadingTime, currentReadingTime)
    if (organizations.isEmpty()) {
        return
    }
    organizations.size.log()
    organizations.flatMap { it.counters }.size.log()

    val dc = dataContextWithTimestampedDb(databasesDir = "./databases", dbNameSuffix = "xls")
    transaction(dc) {
        dc.insertAll(organizations)
    }

    transaction(dc) {
        dc.loadAll().size.println()
    }
}


private fun List<OrganizationsXlsReader.Record>.toOrganizations(
    prevReadingTime: LocalDateTime,
    currentReadingTime: LocalDateTime
): List<Organization> {
    val organizations = mutableListOf<Organization>()
    val recs = this.toMutableList()
    while (recs.isNotEmpty()) {
        val rec = recs.removeAt(0)
        assert(rec.organization.isNotEmpty())
        assert(rec.serialNumber.isNotEmpty())

        // предыдущие - за апрель
        val counter = Counter(
            id = -1,
            serialNumber = rec.serialNumber,
            organizationId = -1,
            K = rec.K,
            readings = listOf(
                CounterReading(
                    id = -1,
                    counterId = -1,
                    reading = rec.prevReading,
                    readingTime = prevReadingTime,
                    user = "Саша",
                    comment = null,
                    synchronized = false,
                    syncTime = null,
                    serverId = null
                ),
                CounterReading(
                    id = -1,
                    counterId = -1,
                    reading = rec.currentReading,
                    readingTime = currentReadingTime,
                    user = "Саша",
                    comment = null,
                    synchronized = false,
                    syncTime = null,
                    serverId = null
                )
            ),
            comment = rec.notes,
            importOrder = rec.importOrder
        )

        var organization = null as Organization?
        var newOrganization = false
        if (rec.group != null) {
            organization = organizations.firstOrNull { it.name == rec.organization }
        }

        if (organization == null) {
            organization = Organization(
                id = -1,
                orgStructureUnitId = -1,
                orgStructureUnit = null,
                name = rec.organization,
                counters = mutableListOf(),
                comment = null,
                importOrder = rec.importOrder
            )
            newOrganization = true
        }

        organization.counters.add(counter)

        if (newOrganization) {
            val existingOrganizationWithName = organizations.firstOrNull { it.name == organization.name }
            if (existingOrganizationWithName != null) {
                throw Exception("Organization with name ${organization.name} already exists with id ${existingOrganizationWithName.id}")
            }

            organizations.add(organization)
        }
    }

    return organizations
}


