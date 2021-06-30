import com.tavrida.energysales.data_access.models.*
import database_creation.utils.log
import database_creation.utils.padStartEx
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private fun main() {
    val dbDir = "./databases/${currentDateStamp()}"
        .also {
            File(it).mkdirs()
        }
    val dbName = "ENERGY_SALES_SYNTHETIC"
    val consumers = syntheticData(500)
    insertAll(dbDir, dbName, consumers)
}

private fun currentDateStamp() = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

private fun syntheticData(nOfConsumers: Int): List<Consumer> {
    val user = "Саша"

    fun readings(
        consumerPos: Int,
        counterPos: Int,
        serialNumber: String,
        K: Double
    ): Pair<List<CounterReading>, PrevCounterReading> {
        val now = LocalDateTime.now()
        val monthAgo = now.minusMonths(1)
        val readingMonthAgo = consumerPos * 100.0
        val readingDeltaForMonth = consumerPos * 10.0 + counterPos
        val readings = listOf(
            CounterReading(
                id = -1,
                counterId = -1,
                reading = readingMonthAgo,
                readingTime = monthAgo,
                user = user,
                comment = "Это показания $monthAgo",
                synchronized = false,
                syncTime = null,
                serverId = null
            ),
            CounterReading(
                id = -1,
                counterId = -1,
                reading = readingMonthAgo + readingDeltaForMonth,
                readingTime = now,
                user = user,
                comment = "Это показания $now",
                synchronized = false,
                syncTime = null,
                serverId = null
            )
        )
        val prevReading = PrevCounterReading(
            id = -1,
            counterId = -1,
            reading = readingMonthAgo,
            consumption = readingDeltaForMonth * K,
            readDate = monthAgo.toLocalDate(),
            comment = "This is prev reading of $serialNumber"
        )
        return readings to prevReading
    }

    var counterImportPos = 0

    val maxLen = nOfConsumers.toString().length
    return (1..nOfConsumers).map { consumerPos ->
        val consumerName = "Потребитель_${consumerPos.padStartEx(maxLen, '0')}"
        val oneOrTwo = if (consumerPos % 2 == 1) (1..1) else (1..2)
        val counters = oneOrTwo.map { counterPos ->
            val serialNumber = "${consumerPos}0$counterPos"
            val K = if (counterPos % 2 == 0) 1.0 else 50.0
            val (readings, prevReading) = readings(consumerPos, counterPos, serialNumber, K)
            Counter(
                id = -1,
                serialNumber = serialNumber,
                consumerId = -1,
                K = K,
                prevReading = prevReading,
                readings = readings,
                comment = "Счетчик $serialNumber потребителя $consumerName!!",
                importOrder = ++counterImportPos
            )

        }

        Consumer(
            id = -1,
            name = consumerName,
            comment = "this is consumer №$consumerPos",
            counters = counters.toMutableList(),
            importOrder = consumerPos
        )
    }
}

private fun insertAll(dbDir: String, dbName: String, consumers: List<Consumer>) {
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

