package main.kotlin.com.tavrida.energysales

import main.kotlin.com.tavrida.energysales.server.CounterReadingSynchronizer
import main.kotlin.com.tavrida.energysales.server.ServerApplication

fun main() {
    ServerApplication(
        8080,
        waitAfterStart = true,
        counterReadingSynchronizer = {
            CounterReadingSynchronizer()
        }
    ).start()
}