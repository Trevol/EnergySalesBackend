package com.tavrida.energysales.server.services

import com.tavrida.energysales.data_access.tables.CounterReadingsTable
import com.tavrida.energysales.data_access.models.*
import com.tavrida.energysales.api.mobile.data_contract.*
import com.tavrida.utils.log
import org.jetbrains.exposed.sql.insertAndGetId
import java.time.LocalDate
import java.time.LocalDateTime

class CounterReadingSynchronizer(private val dataContext: DataContext) {

    fun getRecentData(): List<OrganizationData> {
        return dataContext.selectAllOrganizations()
            .map { it.toOrganizationData() }
    }

    fun uploadReadings(items: List<CounterReadingItem>): List<CounterReadingIdMapping> {
        "syncInRealMode called. Items num: ${items.size}".log()
        if (items.isEmpty()) {
            return listOf()
        }
        val counters = dataContext.loadCounters()
        items.checkCounters(counters)

        val now = LocalDateTime.now()
        return transaction(dataContext) {
            val resp = mutableListOf<CounterReadingIdMapping>()
            items.forEach {
                val counter = counters[it.counterId]!!
                val duplicate =
                    counter.lastReading?.let { r -> r.reading == it.reading && r.readingTime == it.readingTime }
                        ?: false
                if (duplicate) {
                    return@forEach
                }
                val serverReadingId = CounterReadingsTable.insertAndGetId { tab ->
                    tab[counterId] = it.counterId
                    tab[reading] = it.reading
                    tab[readingTime] = it.readingTime
                    tab[user] = it.user
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
        private fun DataContext.loadCounters() = selectAllOrganizations().flatMap { it.counters }.associateBy { it.id }

        fun List<CounterReadingItem>.checkCounters(counters: Map<Int, Counter>) =
            onEach {
                if (counters[it.counterId] == null) {
                    throw Exception("Счетчик не найден по id ${it.counterId}")
                }
            }
    }
}

private fun Organization.toOrganizationData(): OrganizationData {
    return OrganizationData(
        id = id,
        name = name,
        counters = counters.map { it.toCounterData() },
        comment = comment,
        importOrder = importOrder
    )
}

private fun Counter.toCounterData(): CounterData {
    return CounterData(
        id = id,
        serialNumber = serialNumber,
        organizationId = organizationId,
        K = K,
        prevReading = prevReadingData(),
        comment = comment,
        importOrder = importOrder
    )
}

private fun Counter.prevReadingData(): PrevCounterReadingData {
    val prev = prevReading(LocalDate.now())!!
    val prevPrev = prevReading(prev.readingTime.toLocalDate())
    return PrevCounterReadingData(
        id = prev.id,
        counterId = prev.counterId,
        reading = prev.reading,
        consumption = if (prevPrev == null) 0.0 else (prev.reading - prevPrev.reading) * K,
        readDate = prev.readingTime.toLocalDate(),
        comment = prev.comment
    )
}