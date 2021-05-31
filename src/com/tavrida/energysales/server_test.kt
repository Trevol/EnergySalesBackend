package com.tavrida.energysales

import com.tavrida.energysales.apiClient.CounterReadingSyncApiClient
import com.tavrida.energysales.data_contract.CounterReadingSyncItem
import com.tavrida.energysales.db.DatabaseInstance
import com.tavrida.energysales.server.CounterReadingSynchronizer
import com.tavrida.energysales.server.CounterReadingUIController
import com.tavrida.energysales.server.ServerApplication
import com.tavrida.energysales.server.serverModule
import com.tavrida.utils.log
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

suspend fun main() {


    val items = listOf(
        CounterReadingSyncItem(
            id = 1,
            user = "Саша",
            counterId = 1,
            reading = 999.0,
            readingTime = LocalDateTime.now().let { ZonedDateTime.of(it, ZoneId.systemDefault()) }.toInstant()
                .toEpochMilli(),
            comment = null
        )

    )

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
            it.sync(items, testMode = true).log()
        }
    }

}