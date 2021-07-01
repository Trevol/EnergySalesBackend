package com.tavrida.energysales

import com.tavrida.energysales.data_access.DatabaseInstance
import com.tavrida.energysales.server.CounterReadingSynchronizer
import com.tavrida.energysales.server.CounterReadingUIController
import com.tavrida.energysales.server.ServerApplication
import com.tavrida.energysales.server.serverModule
import java.io.File

fun main() {
    val dbDir = File("./databases")
    val dbName = "ENERGY_SALES_TEST_2021-07-01"
    val db = DatabaseInstance.get(dbDir, dbName)

    ServerApplication(
        8080,
        waitAfterStart = true,
        module = {
            serverModule(
                uiController = { CounterReadingUIController(db) },
                synchronizer = { CounterReadingSynchronizer(db) }
            )
        }
    ).start()
}
