import com.tavrida.energysales.data_access.models.*
import database_creation.utils.log
import database_creation.utils.println
import database_creation.xlsx.reader.ImportXlsReader
import database_creation.xlsx.reader.XlsRecord
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

fun main() {
    TODO()
    val consumers = "./databases/xlsx/import 2021 05 май TMP.xlsx"
        .let { ImportXlsReader.read(it) }
        .toConsumers()
    if (consumers.isEmpty()) {
        return
    }
    consumers.size.log()

    val currentDateStamp = currentDateStamp()
    val dbDir = "./databases/$currentDateStamp".also {
        File(it).mkdirs()
    }
    val dbName = "ENERGY_SALES_$currentDateStamp"
    val dc = DbInstance(dbDir, dbName)
        .get(recreate = true)
        .let { DataContext(it) }

    transaction(dc) {
        dc.insertAll(consumers)
    }

    transaction(dc) {
        dc.loadAll().size.println()
    }
}

private fun currentDateStamp() = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

fun List<XlsRecord>.toConsumers(): List<Consumer> {
    val prevReadingTime = LocalDateTime.of(2021, 4, 29, 10, 30, 30) //29.04.2021 10:30:30
    val currentReadingTime = LocalDateTime.of(2021, 6, 1, 10, 30, 30) //01.06.2021 10:30:30

    val consumers = mutableListOf<Consumer>()
    val recs = this.toMutableList()
    while (recs.isNotEmpty()) {
        val rec = recs.removeAt(0)
        assert(rec.consumer.isNotEmpty())
        assert(rec.serialNumber.isNotEmpty())

        // предыдущие - за апрель
        val counter = Counter(
            id = -1,
            serialNumber = rec.serialNumber,
            consumerId = -1,
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

        var consumer = null as Consumer?
        var newConsumer = false
        if (rec.group != null) {
            consumer = consumers.firstOrNull { it.name == rec.consumer }
        }

        if (consumer == null) {
            consumer = Consumer(
                id = -1,
                name = rec.consumer,
                counters = mutableListOf(),
                comment = null,
                importOrder = rec.importOrder
            )
            newConsumer = true
        }

        consumer.counters.add(counter)

        if (newConsumer) {
            val existingConsumerWithName = consumers.firstOrNull { it.name == consumer.name }
            if (existingConsumerWithName != null) {
                throw Exception("Consumer with name ${consumer.name} already exists with id ${existingConsumerWithName.id}")
            }

            consumers.add(consumer)
        }
    }

    return consumers
}


