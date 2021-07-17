package com.tavrida.energysales.server.ktor

import com.tavrida.energysales.server.routing.api
import com.tavrida.energysales.server.routing.ui
import com.tavrida.utils.di.ApplicationCallKoinScope
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.http.*
import org.koin.core.module.Module
import org.koin.ktor.ext.Koin
import java.time.Duration

internal fun Application.serverModule(diModule: Module, callScopeName: String) {
    install(Koin) {
        modules(diModule)
    }
    install(ApplicationCallKoinScope) {
        scopeName = callScopeName
    }
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
        ui()
        api()
    }
}

