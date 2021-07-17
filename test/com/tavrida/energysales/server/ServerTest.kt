package com.tavrida.energysales.server

import com.tavrida.energysales.api.client.CounterReadingSyncApiClient
import com.tavrida.energysales.data_access.DatabaseInstance
import com.tavrida.energysales.di.ApplicationCallScopeName
import com.tavrida.energysales.di.backendServicesContainer
import com.tavrida.energysales.server.ktor.ServerApplication
import com.tavrida.energysales.server.ktor.serverModule
import com.tavrida.energysales.server.settings.BackendSettings
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.test.Test


class ServerTest {
    @Test
    fun main() = runBlocking<Unit> {
        val settings = BackendSettings(
            port = 8082,
            dbDir = File("./databases"),
            dbName = "ENERGY_SALES_2021-07-01"
        )

        ServerApplication(
            settings.port,
            waitAfterStart = false,
            module = {
                serverModule(
                    backendServicesContainer(settings),
                    ApplicationCallScopeName
                )
            }
        ).start().use {
            CounterReadingSyncApiClient("http://0.0.0.0:${settings.port}").use {
                it.getRecentData()
                println(it.hello())
            }
        }

    }
}
