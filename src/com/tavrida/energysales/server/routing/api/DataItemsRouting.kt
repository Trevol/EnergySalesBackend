package com.tavrida.energysales.server.routing.api

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

@Deprecated("This is test routing. Remove it")
fun Route.dataItemsRouting() {
    route("/data_items") {
        get {
            val n = call.parameters["n"]?.toIntOrNull() ?: 25
            val message = (1..n).map {
                mapOf("firstName" to "First Name $it", "lastName" to "Last Name $it")
            }
            call.respond(message)
        }
    }
}