package com.tavrida.energysales

import com.tavrida.energysales.db.DatabaseInstance
import com.tavrida.energysales.server.CounterReadingSynchronizer
import com.tavrida.energysales.server.CounterReadingUIController
import com.tavrida.energysales.server.ServerApplication
import com.tavrida.energysales.server.serverModule
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
    val dbDir = File("db")
    val db = DatabaseInstance.get(dbDir)

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
