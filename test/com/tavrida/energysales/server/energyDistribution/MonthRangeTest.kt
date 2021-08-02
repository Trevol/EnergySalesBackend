package com.tavrida.energysales.server.energyDistribution

import com.tavrida.energysales.data_access.DatabaseInstance
import com.tavrida.energysales.data_access.dbmodel.tables.CounterReadingsTable
import database_creation.utils.println
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.min
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import java.io.File

class MonthRangeTest {
    @Test
    fun `how to select min and max readingTime`() {
        val dbDir = File("./databases")
        val dbName = "ENERGY_SALES_2021-07-01"
        val db = DatabaseInstance.get(dbDir, dbName)
        transaction(db) {
            val CRT = CounterReadingsTable
            val (min, max) = CRT.slice(CRT.readingTime.min(), CRT.readingTime.max())
                .selectAll()
                .first()
                .let {
                    it[CRT.readingTime.min()]?.toLocalDate() to it[CRT.readingTime.max()]?.toLocalDate()
                }
            "$min   $max".println()
        }
    }
}