package com.tavrida.energysales.server.routing.api

import com.tavrida.energysales.api.data_contract.HelloResponse
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.LocalDate

fun Route.helloRouting() {
    route("/hello") {
        get {
            call.respond(HelloResponse("Hello!", LocalDate.now()))
        }
    }
}