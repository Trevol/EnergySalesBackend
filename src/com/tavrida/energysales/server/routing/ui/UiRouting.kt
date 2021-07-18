package com.tavrida.energysales.server.routing.ui

import com.tavrida.energysales.server.services.CounterReadingUIController
import com.tavrida.utils.di.getCallScoped
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.routing.*
import kotlin.text.get

fun Route.ui() {
    route("/") {
        get {
            call.respondHtml {
                call.getCallScoped<CounterReadingUIController>().indexView(this)
            }
        }
    }

    route("/download") {
        get {
            call.getCallScoped<CounterReadingUIController>().downloadAsXlsx(call)
        }
    }
}