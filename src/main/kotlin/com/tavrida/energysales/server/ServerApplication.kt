package main.kotlin.com.tavrida.energysales.server

import io.ktor.server.cio.*
import io.ktor.server.engine.*

class ServerApplication(
    port: Int,
    val waitAfterStart: Boolean,
    counterReadingSynchronizer: () -> CounterReadingSynchronizer
) : AutoCloseable {
    private val server = embeddedServer(CIO, port,
        module = {
            serverModule(counterReadingSynchronizer = counterReadingSynchronizer)
        })

    override fun close() {
        stop()
    }

    private fun stop(gracePeriodMillis: Long = 1000, timeoutMillis: Long = 1000) {
        server.stop(gracePeriodMillis, timeoutMillis)
    }

    fun start() = this.also { server.start(waitAfterStart) }
}