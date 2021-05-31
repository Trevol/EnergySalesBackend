package com.tavrida.energysales.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.tavrida.energysales.data_contract.CounterReadingSyncRequest
import io.ktor.html.*
import java.io.File

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
            route("/syncReadings") {
                post {
                    val data = call.receive<CounterReadingSyncRequest>()
                    val idMappings = withContext(Dispatchers.IO) {
                        synchronizer().sync(data.items, data.testMode)
                    }
                    call.respond(idMappings)
                }
            }
        }
    }
}