import com.tavrida.energysales.data_access.models.*
import database_creation.utils.println
import database_creation.xlsx.reader.ImportXlsReader
import database_creation.xlsx.reader.XlsRecord
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

fun main() {
    val consumers = "/hdd/TMP/Распределение 2021/import.xlsx"
        .let { ImportXlsReader.read(it) }
        .toConsumers()
    if (consumers.isEmpty()) {
        return
    }

    val dc = DbInstance("./databases/", "ENERGY_SALES_MOBILE")
        .get(recreate = true)
        .let { DataContext(it) }

    transaction(dc) {
        dc.insertAll(consumers)
    }

    transaction(dc) {
        dc.loadAll().size.println()
    }
}

fun List<XlsRecord>.toConsumers(): List<Consumer> {
    val aprilMonth = LocalDate.of(2021, Month.APRIL, 1)
    val currentReadingTime = LocalDateTime.of(2021, Month.MAY, 1, 10, 30, 30)

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
            prevReading = PrevCounterReading(
                id = -1,
                counterId = -1,
                reading = rec.currentReading, //за апрель - это уже предыдущие, собираем за май
                consumption = rec.consumption, //потреблено за апрель - сравниваем с потреблением за май
                readDate = aprilMonth,
                comment = null
            ),
            readings = listOf(
                //текущих НЕТ - мы их только будем собирать
                /*CounterReading(
                    id = -1,
                    counterId = -1,
                    reading = rec.currentReading,
                    readTime = currentReadingTime,
                    comment = null
                )*/
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


