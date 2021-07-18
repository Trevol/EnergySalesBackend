package com.tavrida.energysales.server.routing.api

import io.ktor.routing.*

fun Route.api() {
    route("/api") {
        dataItemsRouting()
        helloRouting()
        mobileAppRouting()
    }
}

fun Route.energyDistributionRouting(){
    route("/energy-distribution"){
        post {

        }
    }
}