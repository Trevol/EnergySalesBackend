package com.tavrida.energysales.server

import com.tavrida.energysales.data_access.dbmodel.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.models.*
import com.tavrida.energysales.data_contract.CounterReadingIdMapping
import com.tavrida.energysales.data_contract.CounterReadingItem
import com.tavrida.utils.log
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.*
import java.time.LocalDateTime

class CounterReadingSynchronizer(val db: Database) {
    private val dataContext = DataContext(db)

    suspend fun sync(items: List<CounterReadingItem>): List<CounterReadingIdMapping> {
        return syncInRealMode(items)
    }

    private suspend fun syncInRealMode(items: List<CounterReadingItem>): List<CounterReadingIdMapping> {
        "syncInRealMode called. Items num: ${items.size}".log()
        if (items.isEmpty()) {
            return listOf()
        }
        val counters = dataContext.loadCounters()
        items.checkCounters(counters)

        val now = LocalDateTime.now()
        return transaction(db) {
            val resp = mutableListOf<CounterReadingIdMapping>()
            items.forEach {
                val counter = counters[it.counterId]!!
                val duplicate =
                    counter.lastReading?.let { r -> r.reading == it.reading && r.readingTime == it.readingTimeAsDateTime }
                        ?: false
                if (duplicate) {
                    return@forEach
                }
                val serverReadingId = CounterReadingsTable.insertAndGetId { tab ->
                    tab[counterId] = it.counterId
                    tab[reading] = it.reading
                    tab[readingTime] = it.readingTimeAsDateTime
                    tab[comment] = it.comment
                    tab[synchronized] = true
                    tab[syncTime] = now
                }
                resp.add(CounterReadingIdMapping(id = it.id, serverId = serverReadingId.value))
            }
            resp
        }

    }

    private companion object {
        private fun DataContext.loadCounters() = loadAll().flatMap { it.counters }.associateBy { it.id }
        fun List<CounterReadingItem>.checkCounters(counters: Map<Int, Counter>) =
            onEach {
                if (counters[it.counterId] == null) {
                    throw Exception("Счетчик не найден по id ${it.counterId}")
                }
            }
    }
}