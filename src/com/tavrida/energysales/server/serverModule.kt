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

internal fun Application.serverModule(counterReadingSynchronizer: () -> CounterReadingSynchronizer) {
    install(ContentNegotiation) { json() }
    routing {

        route("/") {
            get {
                call.respond("Сервер работает!")
            }
        }

        route("/api") {
            route("/syncReadings") {
                post {
                    val data = call.receive<CounterReadingSyncRequest>()
                    val idMappings = withContext(Dispatchers.IO) {
                        counterReadingSynchronizer().sync(data.items, data.testMode)
                    }
                    call.respond(idMappings)
                }
            }
        }
    }
}