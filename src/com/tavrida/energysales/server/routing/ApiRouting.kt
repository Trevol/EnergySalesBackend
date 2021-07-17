package com.tavrida.energysales.server.routing

import com.tavrida.energysales.api.data_contract.CounterReadingItem
import com.tavrida.energysales.server.routing.dataItemsRouting
import com.tavrida.energysales.server.routing.helloRouting
import com.tavrida.energysales.server.services.CounterReadingSynchronizer
import com.tavrida.utils.di.getCallScoped
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.text.get

fun Route.api() {
    route("/api") {
        dataItemsRouting()
        helloRouting()

        route("/mobile") {

            route("/readings") { // /api/mobile/readings
                post {
                    val readings = call.receive<List<CounterReadingItem>>()
                    val idMappings = withContext(Dispatchers.IO) {
                        call.getCallScoped<CounterReadingSynchronizer>().uploadReadings(readings)
                    }
                    call.respond(idMappings)
                }
            }

            route("/recent_data") {  // /api/mobile/recent_data
                get {
                    val consumers = withContext(Dispatchers.IO) {
                        call.getCallScoped<CounterReadingSynchronizer>().getRecentData()
                    }
                    call.respond(consumers)
                }
            }
        }
    }

}