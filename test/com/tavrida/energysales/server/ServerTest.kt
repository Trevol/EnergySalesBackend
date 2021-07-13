package com.tavrida.energysales.server

import com.tavrida.energysales.api.client.CounterReadingSyncApiClient
import com.tavrida.energysales.data_access.DatabaseInstance
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.test.Test


class ServerTest {
    @Test
    fun main() = runBlocking<Unit> {

        val dbDir = File("./databases")
        val dbName = "ENERGY_SALES_2021-07-01"
        val db = DatabaseInstance.get(dbDir, dbName)

        val port = 8080

        ServerApplication(
            port,
            waitAfterStart = false,
            module = {
                serverModule(
                    uiController = { CounterReadingUIController(db) },
                    synchronizer = { CounterReadingSynchronizer(db) }
                )
            }
        ).start().use {
            CounterReadingSyncApiClient("http://0.0.0.0:$port").use {
                it.getRecentData().size
                println(it.hello())
            }
        }

    }
}
