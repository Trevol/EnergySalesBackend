package com.tavrida.energysales.data_access.models

import com.tavrida.energysales.data_access.tables.OrganizationsTable
import com.tavrida.energysales.data_access.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.tables.CountersTable
import com.tavrida.energysales.data_access.tables.OrganizationStructureUnits
import io.kotest.assertions.all
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface IDataContext {
    fun selectAllOrganizations(): List<Organization>
    fun selectOrganizationStructureUnits(): List<OrganizationStructureUnit>
    fun selectCounter(counterId: Int): Counter
    fun selectOrganizations(orgStructureUnitId: Int, recursive: Boolean = true): List<Organization>
    fun updateReading(reading: CounterReading)
    fun createReading(newReading: CounterReading)
    fun updateSyncData(unsynchronized: List<CounterReading>)

}

class DataContext(val db: Database) : IDataContext {
    override fun selectOrganizationStructureUnits() = transaction(db) {
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

    override fun selectAllOrganizations() = transaction(db) {
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

    override fun selectOrganizations(orgStructureUnitId: Int, recursive: Boolean): List<Organization> {
        //TODO: извлекать только НЕОБХОДИМЫЕ данные из БД!!! То, что ниже - временное, для ускорения разработки фичи
        val allOrganizations = selectAllOrganizations()
        return if (!recursive) {
            allOrganizations.filter { it.orgStructureUnitId == orgStructureUnitId }
        } else {
            val units = selectOrganizationStructureUnits()
            units.first { it.id == orgStructureUnitId }
                .flatSubtree(units)
                .flatMap { unit -> allOrganizations.filter { it.orgStructureUnitId == unit.id } }
        }
    }

    private fun OrganizationStructureUnit.flatSubtree(allUnits: List<OrganizationStructureUnit>): List<OrganizationStructureUnit> {
        val flatSubtree = mutableListOf(this)
        val directChildren = allUnits.filter { it.parentId == id }
        flatSubtree.addAll(
            directChildren.flatMap { it.flatSubtree(allUnits) }
        )
        return flatSubtree
    }

    override fun selectCounter(counterId: Int): Counter {
        return transaction(db) {
            val readings = CounterReadingsTable
                .let { CR ->
                    CR.select { CR.counterId eq counterId }
                }
                .map { it.toCounterReading() }

            CountersTable.let { C ->
                C.select { C.id eq counterId }
                    .first().toCounter(readings)
            }
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

        val readings = readingRows.map { it.toCounterReading() }

        val counters = countersRows.map {
            val counterId = it[CountersTable.id].value
            it.toCounter(readings.filter { it.counterId == counterId })
        }

        return organizationRows.map {
            val organizationId = it[OrganizationsTable.id].value
            it.toOrganization(counters.filter { it.organizationId == organizationId })
        }
    }
}

private fun ResultRow.toOrganization(counters: List<Counter>): Organization {
    val T = OrganizationsTable
    return Organization(
        id = this[T.id].value,
        orgStructureUnitId = this[T.orgStructureUnitId].value,
        orgStructureUnit = null,
        name = this[T.name],
        counters = counters.toMutableList(),
        comment = this[T.comment],
        importOrder = this[T.importOrder]
    )
}

private fun ResultRow.toCounter(readings: MutableList<CounterReading>): Counter {
    val t = CountersTable
    return Counter(
        id = this[t.id].value,
        serialNumber = this[t.serialNumber],
        organizationId = this[t.organizationId].value,
        K = this[t.K],
        readings = readings,
        comment = this[t.comment],
        importOrder = this[t.importOrder]
    )
}

@JvmName("ResultRow_toCounter_list")
private fun ResultRow.toCounter(readings: List<CounterReading>) = toCounter(readings.toMutableList())

private fun ResultRow.toCounterReading(): CounterReading {
    val t = CounterReadingsTable
    return CounterReading(
        id = this[t.id].value,
        counterId = this[t.counterId].value,
        reading = this[t.reading],
        readingTime = this[t.readingTime],
        user = this[t.user],
        comment = this[t.comment],
        synchronized = this[t.synchronized],
        syncTime = this[t.syncTime],
        serverId = this[t.serverId],
    )
}

fun <T> transaction(dc: DataContext, statement: Transaction.() -> T): T =
    transaction(dc.db, statement)