package com.tavrida.energysales

import com.tavrida.energysales.server.CounterReadingSynchronizer
import com.tavrida.energysales.server.ServerApplication

fun main() {
    ServerApplication(
        8080,
        waitAfterStart = true,
        counterReadingSynchronizer = {
            CounterReadingSynchronizer()
        }
    ).start()
}