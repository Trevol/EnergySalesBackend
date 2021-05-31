package com.tavrida.energysales

import com.tavrida.energysales.db.DatabaseInstance
import com.tavrida.energysales.server.CounterReadingSynchronizer
import com.tavrida.energysales.server.ServerApplication
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
    thread {
        Thread.sleep(3000)
        exitProcess(2)
        // throw Exception("Fatal for server")
    }

    val dbDir = File("db")
    ServerApplication(
        8080,
        waitAfterStart = true,
        counterReadingSynchronizer = {
            CounterReadingSynchronizer(DatabaseInstance.get(dbDir))
        }
    ).start()
}
