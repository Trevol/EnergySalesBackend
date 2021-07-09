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
import io.ktor.http.*
import java.time.Duration
import java.time.LocalDate

internal fun Application.serverModule(
    uiController: () -> CounterReadingUIController,
    synchronizer: () -> CounterReadingSynchronizer
) {
    install(ContentNegotiation) { json() }
    install(CORS)
    {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
        maxAgeInSeconds = Duration.ofDays(1).seconds
    }

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

            route("/data_items") {
                get {
                    val n = call.parameters["n"]?.toIntOrNull() ?: 25
                    val message = (1 .. n).map {
                        mapOf("firstName" to "First Name $it", "lastName" to "Last Name $it")
                    }
                    call.respond(message)
                }
            }

            route("/hello") {
                get {
                    call.respond(HelloResponse("Hello!", LocalDate.now()))
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
                        val consumers = withContext(Dispatchers.IO) {
                            synchronizer().getRecentData()
                        }
                        call.respond(consumers)
                    }
                }
            }

        }
    }
}