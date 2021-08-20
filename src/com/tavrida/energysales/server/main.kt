package com.tavrida.energysales.server

import com.tavrida.energysales.di.ApplicationCallScopeName
import com.tavrida.energysales.di.backendServicesContainer
import com.tavrida.energysales.server.ktor.ServerApplication
import com.tavrida.energysales.server.ktor.serverModule
import com.tavrida.energysales.server.settings.BackendSettings
import java.io.File


fun main() {
    val settings = BackendSettings(
        port = 8080,
        dbDir = File("./databases"),
        dbName = "ENERGY_SALES_xls_ALL"
    )

    ServerApplication(
        settings.port,
        waitAfterStart = true,
        module = {
            serverModule(
                backendServicesContainer(settings),
                ApplicationCallScopeName
            )
        }
    ).start()
}
