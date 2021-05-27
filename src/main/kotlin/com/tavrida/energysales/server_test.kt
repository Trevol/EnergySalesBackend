package main.kotlin.com.tavrida.energysales

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import main.kotlin.com.tavrida.energysales.apiClient.CounterReadingSyncApiClient
import main.kotlin.com.tavrida.energysales.data_contract.CounterReadingSyncItem
import main.kotlin.com.tavrida.energysales.server.CounterReadingSynchronizer
import main.kotlin.com.tavrida.energysales.server.ServerApplication
import main.kotlin.com.tavrida.utils.log
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
            comment = ""
        )

    )
    val port = 8080
    ServerApplication(
        port,
        waitAfterStart = false,
        counterReadingSynchronizer = {
            CounterReadingSynchronizer()
        }
    ).start().use {
        CounterReadingSyncApiClient("0.0.0.0", port).use {
            it.sync(items, testMode = true).log()
        }
    }

}