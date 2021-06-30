package com.tavrida.energysales.server

import com.tavrida.energysales.data_contract.CounterReadingItem
import com.tavrida.energysales.data_contract.HelloResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.html.*

internal fun Application.serverModule(
    uiController: () -> CounterReadingUIController,
    synchronizer: () -> CounterReadingSynchronizer
) {
    install(ContentNegotiation) { json() }
    routing {

        route("/") {
            get {
                call.respondHtml {
                    uiController().indexView(this)
                }
            }
        }

        route("/download") {
            get {
                uiController().downloadAsXlsx(call)
            }
        }

        route("/api") {

            route("/hello") {
                get {
                    call.respond(HelloResponse("Hello!"))
                }
            }


            route("/mobile") {

                route("/readings") { // /api/mobile/readings
                    post {
                        val readings = call.receive<List<CounterReadingItem>>()
                        val idMappings = withContext(Dispatchers.IO) {
                            synchronizer().uploadReadings(readings)
                        }
                        call.respond(idMappings)
                    }
                }

                route("/recent_data") {  // /api/mobile/recent_data
                    get {
                        call.respond(1234)
                    }
                }
            }

        }
    }
}