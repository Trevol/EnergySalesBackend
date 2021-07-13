package com.tavrida.energysales.server.routing

import com.tavrida.energysales.data_contract.HelloResponse
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.LocalDate
import kotlin.text.get

fun Route.helloRouting() {
    route("/hello") {
        get {
            call.respond(HelloResponse("Hello!", LocalDate.now()))
        }
    }
}