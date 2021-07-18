package com.tavrida.energysales.server.routing.api

import com.tavrida.utils.ktor.respondTo
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

@Deprecated("This is test routing. Remove it")
fun Route.dataItemsRouting() {
    route("/data_items") {
        get {
            (call.parameters["n"]?.toIntOrNull() ?: 25)
                .let { 1..it }
                .map {
                    mapOf("firstName" to "First Name $it", "lastName" to "Last Name $it")
                }
                .respondTo(call)
        }
    }
}