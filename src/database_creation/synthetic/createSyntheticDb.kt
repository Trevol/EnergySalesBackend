package database_creation.synthetic

import com.tavrida.energysales.data_access.models.*
import database_creation.DbInstance
import database_creation.utils.currentDateStamp
import database_creation.utils.log
import database_creation.utils.padStartEx
import java.io.File
import java.time.LocalDateTime

private fun main() {
    TODO()
    val currentDateStamp = currentDateStamp()
    val dbDir = "./databases/$currentDateStamp".also {
        File(it).mkdirs()
    }
    val dbName = "ENERGY_SALES_SYNTHETIC_$currentDateStamp"
    val nOfOrganizations = 5
    val organizations = syntheticData(nOfOrganizations)
    insertAll(dbDir, dbName, organizations)
}

private fun syntheticData(nOfOrganizations: Int): List<Organization> {
    val user = "Саша"

    fun readings(
        organizationPos: Int,
        counterPos: Int,
        serialNumber: String,
        K: Double
    ): MutableList<CounterReading> {
        val (monthAgo, twoMonthAgo) = LocalDateTime.now().let { now ->
            listOf(now.minusMonths(1), now.minusMonths(2))
        }

        val readingMonthAgo = organizationPos * 100.0
        val readingDeltaForMonth = organizationPos * 10.0 + counterPos
        val readings = mutableListOf(
            CounterReading(
                id = -1,
                counterId = -1,
                reading = readingMonthAgo - readingDeltaForMonth,
                readingTime = twoMonthAgo,
                user = user,
                comment = "Это показания $twoMonthAgo",
                synchronized = false,
                syncTime = null,
                serverId = null
            ),
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
            )
        )
        return readings
    }

    var counterImportPos = 0

    val maxLen = nOfOrganizations.toString().length
    return (1..nOfOrganizations).map { organizationPos ->
        val organizationName = "Потребитель_${organizationPos.padStartEx(maxLen, '0')}"
        val oneOrTwo = if (organizationPos % 2 == 1) (1..1) else (1..2)
        val counters = oneOrTwo.map { counterPos ->
            val serialNumber = "${organizationPos}0$counterPos"
            val K = if (counterPos % 2 == 0) 1.0 else 50.0
            val readings = readings(organizationPos, counterPos, serialNumber, K)
            Counter(
                id = -1,
                serialNumber = serialNumber,
                organizationId = -1,
                K = K,
                readings = readings,
                comment = "Счетчик $serialNumber потребителя $organizationName!!",
                importOrder = ++counterImportPos
            )

        }

        Organization(
            id = -1,
            orgStructureUnitId = -1,
            orgStructureUnit = null,
            name = organizationName,
            comment = "this is organization №$organizationPos",
            counters = counters.toMutableList(),
            importOrder = organizationPos
        )
    }
}

private fun insertAll(dbDir: String, dbName: String, organizations: List<Organization>) {
    val dc = DbInstance(dbDir, dbName)
        .get(recreate = true)
        .let { DataContext(it) }

    transaction(dc) {
        dc.insertAll(organizations)
    }

    transaction(dc) {
        val allOrganizations = dc.selectAllOrganizations()
        allOrganizations.size.log()
    }
}

