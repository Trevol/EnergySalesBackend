package com.tavrida.energysales.server.routing.api

import com.tavrida.energysales.api.mobile.data_contract.CounterReadingItem
import com.tavrida.energysales.server.services.CounterReadingSynchronizer
import com.tavrida.utils.di.getCallScoped
import com.tavrida.utils.ktor.respondTo
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.text.get

fun Route.mobileAppRouting() {
    route("/mobile") {

        route("/readings") { // /api/mobile/readings
            post {
                call.receive<List<CounterReadingItem>>()
                    .let { readings ->
                        withContext(Dispatchers.IO) {
                            call.getCallScoped<CounterReadingSynchronizer>().uploadReadings(readings)
                        }
                    }.respondTo(call)
            }
        }

        route("/recent_data") {  // /api/mobile/recent_data
            get {
                withContext(Dispatchers.IO) {
                    call.getCallScoped<CounterReadingSynchronizer>().getRecentData()
                }.respondTo(call)
            }
        }
    }

}