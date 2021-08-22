package com.tavrida.energysales.data_access.models

import com.tavrida.energysales.data_access.tables.OrganizationsTable
import com.tavrida.energysales.data_access.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.tables.CountersTable
import com.tavrida.energysales.data_access.tables.OrganizationStructureUnits
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface IDataContext {
    fun loadAllOrganizations(): List<Organization>
    fun updateReading(reading: CounterReading)
    fun createReading(newReading: CounterReading)
    fun updateSyncData(unsynchronized: List<CounterReading>)
}

class DataContext(val db: Database) : IDataContext {
    fun selectOrganizationStructureUnits() = transaction(db) {
        val t = OrganizationStructureUnits
        OrganizationStructureUnits.selectAll().map {
            OrganizationStructureUnit(
                id = it[t.id].value,
                parentId = it[t.parentId],
                name = it[t.name],
                comment = it[t.comment]
            )
        }
    }

    fun insertAll(organizations: List<Organization>) {
        if (organizations.isEmpty()) {
            return
        }
        transaction(db) {
            for (organization in organizations) {
                val orgId = OrganizationsTable.insertAndGetId {
                    it[name] = organization.name
                    it[orgStructureUnitId] = organization.orgStructureUnitId
                    it[comment] = organization.comment
                    it[importOrder] = organization.importOrder
                }.value

                for (counter in organization.counters) {
                    val counterId = CountersTable.insertAndGetId {
                        it[serialNumber] = counter.serialNumber
                        it[organizationId] = orgId
                        it[K] = counter.K
                        it[comment] = counter.comment
                        it[importOrder] = counter.importOrder
                    }.value

                    for (reading in counter.readings) {
                        CounterReadingsTable.insert {
                            it[this.counterId] = counterId
                            it[this.reading] = reading.reading
                            it[readingTime] = reading.readingTime
                            it[user] = reading.user
                            it[comment] = reading.comment
                            it[synchronized] = reading.synchronized
                            it[syncTime] = reading.syncTime
                            it[serverId] = reading.serverId
                        }
                    }
                }
            }
        }
    }

    override fun loadAllOrganizations() = transaction(db) {
        val organizationRows = OrganizationsTable.selectAll()
            .orderBy(OrganizationsTable.importOrder)
            .toList()
        if (organizationRows.isEmpty()) {
            listOf()
        } else {
            val counterRows = CountersTable.selectAll()
                .orderBy(CountersTable.importOrder)
                .toList()
            val readingRows = CounterReadingsTable.selectAll().toList()
            connectOrganizationEntities(organizationRows, counterRows, readingRows)
        }
    }

    override fun updateSyncData(unsynchronized: List<CounterReading>) {
        TODO()
        /*if (unsynchronized.isEmpty()) {
            return
        }
        transaction(db) {
            for (r in unsynchronized) {
                CounterReadingsTable.update({ CounterReadingsTable.id eq r.id }) {
                    it[synchronized] = r.synchronized
                    it[syncTime] = r.syncTime
                    it[serverId] = r.serverId
                }
            }
        }*/
    }

    override fun updateReading(reading: CounterReading) {
        transaction(db) {
            CounterReadingsTable.update({ CounterReadingsTable.id eq reading.id }) {
                it[this.reading] = reading.reading
                it[readingTime] = reading.readingTime
                it[comment] = reading.comment
                it[synchronized] = reading.synchronized
                it[syncTime] = reading.syncTime
                it[serverId] = reading.serverId
            }
        }
    }

    override fun createReading(newReading: CounterReading) {
        transaction(db) {
            val id = CounterReadingsTable.insertAndGetId {
                it[counterId] = newReading.counterId
                it[reading] = newReading.reading
                it[readingTime] = newReading.readingTime
                it[comment] = newReading.comment
                it[synchronized] = newReading.synchronized
                it[syncTime] = newReading.syncTime
                it[serverId] = newReading.serverId
            }
            newReading.id = id.value
        }
    }

    private fun connectOrganizationEntities(
        organizationRows: List<ResultRow>,
        countersRows: List<ResultRow>,
        readingRows: List<ResultRow>
    ): List<Organization> {
        if (organizationRows.isEmpty())
            return listOf()

        val readings = readingRows.map {
            val t = CounterReadingsTable
            CounterReading(
                id = it[t.id].value,
                counterId = it[t.counterId].value,
                reading = it[t.reading],
                readingTime = it[t.readingTime],
                user = it[t.user],
                comment = it[t.comment],
                synchronized = it[t.synchronized],
                syncTime = it[t.syncTime],
                serverId = it[t.serverId],
            )
        }

        val counters = countersRows.map {
            val t = CountersTable
            val counterId = it[t.id].value
            Counter(
                id = counterId,
                serialNumber = it[t.serialNumber],
                organizationId = it[t.organizationId].value,
                K = it[t.K],
                readings = readings.filter { it.counterId == counterId }.toMutableList(),
                comment = it[t.comment],
                importOrder = it[t.importOrder]
            )
        }

        return organizationRows.map {
            val t = OrganizationsTable
            val organizationId = it[t.id].value
            Organization(
                id = organizationId,
                orgStructureUnitId = it[t.orgStructureUnitId].value,
                orgStructureUnit = null,
                name = it[t.name],
                counters = counters.filter { it.organizationId == organizationId }.toMutableList(),
                comment = it[t.comment],
                importOrder = it[t.importOrder]
            )
        }
    }
}

fun <T> transaction(dc: DataContext, statement: Transaction.() -> T): T =
    transaction(dc.db, statement)