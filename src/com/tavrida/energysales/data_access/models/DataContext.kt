package com.tavrida.energysales.data_access.models

import com.tavrida.energysales.data_access.dbmodel.tables.OrganizationsTable
import com.tavrida.energysales.data_access.dbmodel.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.dbmodel.tables.CountersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface IDataContext {
    fun loadAll(): List<Organization>
    fun updateReading(reading: CounterReading)
    fun createReading(newReading: CounterReading)
    fun updateSyncData(unsynchronized: List<CounterReading>)
}

class DataContext(val db: Database) : IDataContext {
    fun insertAll(organizations: List<Organization>) {
        if (organizations.isEmpty()) {
            return
        }
        transaction(db) {
            for (organization in organizations) {
                val orgId = OrganizationsTable.insertAndGetId {
                    it[name] = organization.name
                    it[orgStructureId] = organization.orgStructureId
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

    override fun loadAll() = transaction(db) {
        val consumerRows = OrganizationsTable.selectAll()
            .orderBy(OrganizationsTable.importOrder)
            .toList()
        if (consumerRows.isEmpty()) {
            listOf()
        } else {
            val counterRows = CountersTable.selectAll()
                .orderBy(CountersTable.importOrder)
                .toList()
            val readingRows = CounterReadingsTable.selectAll().toList()
            connectConsumerEntities(consumerRows, counterRows, readingRows)
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

    private fun connectConsumerEntities(
        consumerRows: List<ResultRow>,
        countersRows: List<ResultRow>,
        readingRows: List<ResultRow>
    ): List<Organization> {
        if (consumerRows.isEmpty())
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
                consumerId = it[t.organizationId].value,
                K = it[t.K],
                readings = readings.filter { it.counterId == counterId },
                comment = it[t.comment],
                importOrder = it[t.importOrder]
            )
        }

        return consumerRows.map {
            val t = OrganizationsTable
            val consumerId = it[t.id].value
            Organization(
                id = consumerId,
                orgStructureId = it[t.orgStructureId].value,
                name = it[t.name],
                counters = counters.filter { it.consumerId == consumerId }.toMutableList(),
                comment = it[t.comment],
                importOrder = it[t.importOrder]
            )
        }
    }
}

fun <T> transaction(dc: DataContext, statement: Transaction.() -> T): T =
    transaction(dc.db, statement)