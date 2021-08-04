package database_creation.synthetic

import com.tavrida.energysales.data_access.models.*
import database_creation.DbInstance
import database_creation.utils.currentDateStamp
import database_creation.utils.log
import java.io.File
import java.time.LocalDateTime

private fun main() {
    TODO()
    val currentDateStamp = currentDateStamp()
    val dbDir = "./databases/$currentDateStamp"
        .also {
            File(it).mkdirs()
        }
    val dbName = "ENERGY_SALES_TEST_$currentDateStamp"
    val consumers = testData()
    insertAll(dbDir, dbName, consumers)
}

private fun testData(): List<Organization> {
    val user = "Саша"

    val reading_21_05_01 = CounterReading(
        id = -1,
        counterId = -1,
        reading = 28.0,
        readingTime = LocalDateTime.of(2021, 5, 1, 12, 34),
        user = user,
        comment = "",
        synchronized = false,
        syncTime = null,
        serverId = null
    )
    val reading_21_06_01 = CounterReading(
        id = -1,
        counterId = -1,
        reading = 33.0,
        readingTime = LocalDateTime.of(2021, 6, 1, 11, 21),
        user = user,
        comment = "",
        synchronized = false,
        syncTime = null,
        serverId = null
    )
    val reading_21_06_30 = CounterReading(
        id = -1,
        counterId = -1,
        reading = 44.0,
        readingTime = LocalDateTime.of(2021, 6, 30, 10, 58),
        user = user,
        comment = "",
        synchronized = false,
        syncTime = null,
        serverId = null
    )


    val counter = Counter(
        id = -1,
        serialNumber = "111111",
        consumerId = -1,
        K = 1.0,
        readings = listOf(reading_21_05_01, reading_21_06_01, reading_21_06_30),
        comment = "",
        importOrder = 1
    )
    val consumer = Organization(
        id = -1,
        orgStructureUnitId = -1,
        name = "TestConsumer",
        comment = "",
        counters = mutableListOf(counter),
        importOrder = 1
    )

    return listOf(consumer)
}

private fun insertAll(dbDir: String, dbName: String, consumers: List<Organization>) {
    val dc = DbInstance(dbDir, dbName)
        .get(recreate = true)
        .let { DataContext(it) }

    transaction(dc) {
        dc.insertAll(consumers)
    }

    transaction(dc) {
        val allConsumers = dc.loadAll()
        allConsumers.size.log()
    }
}

