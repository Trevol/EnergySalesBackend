package com.tavrida.energysales.server.routing.api

import io.ktor.routing.*

fun Route.api() {
    route("/api") {
        dataItemsRouting()
        helloRouting()
        mobileAppRouting()
        energyDistributionRouting()
    }
}