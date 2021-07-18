package com.tavrida.utils.ktor

import io.ktor.application.*
import io.ktor.response.*

suspend inline fun <reified T : Any> T.respondTo(call: ApplicationCall) = call.respond(this)