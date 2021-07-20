package com.tavrida.energysales.server.routing.api

import com.tavrida.energysales.energy_distribution.energyDistributionRouting
import io.ktor.routing.*

fun Route.api() {
    route("/api") {
        dataItemsRouting()
        helloRouting()
        mobileAppRouting()
        energyDistributionRouting()
    }
}