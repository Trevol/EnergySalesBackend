package com.tavrida.energysales

import com.tavrida.energysales.apiClient.CounterReadingSyncApiClient
import com.tavrida.energysales.data_contract.CounterReadingItem
import com.tavrida.energysales.data_access.DatabaseInstance
import com.tavrida.energysales.server.CounterReadingSynchronizer
import com.tavrida.energysales.server.CounterReadingUIController
import com.tavrida.energysales.server.ServerApplication
import com.tavrida.energysales.server.serverModule
import java.io.File
import java.time.LocalDateTime

suspend fun main() {

    val items = (1..250).map {
        CounterReadingItem(
            id = 1,
            user = "Саша",
            counterId = it % 190+1,
            reading = 999.0,
            readingTime = LocalDateTime.now(),
            comment = null
        )
    }


    val dbDir = File("db")
    val db = DatabaseInstance.get(dbDir)

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
        CounterReadingSyncApiClient("0.0.0.0", port).use {
            it.sync(items)
        }
    }

}