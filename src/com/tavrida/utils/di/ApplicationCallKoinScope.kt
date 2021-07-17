package com.tavrida.utils.di

import io.ktor.application.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.getKoin
import java.util.*

class ApplicationCallKoinScope {
    class Configuration {
        var scopeName: String = ""
    }

    companion object Feature : ApplicationFeature<Application, Configuration, ApplicationCallKoinScope> {
        override val key = AttributeKey<ApplicationCallKoinScope>("KoinApplicationCallScope")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): ApplicationCallKoinScope {
            if (pipeline.featureOrNull(Koin) == null) {
                throw KoinApplicationCallScopeException("Koin feature is required for KoinApplicationCallScope. Install Koin feature before KoinApplicationCallScope.")
            }

            val configuration = Configuration().apply(configure)
            if (configuration.scopeName.isEmpty()) {
                throw KoinApplicationCallScopeException("scopeName is not configured")
            }

            val beforeSetup = PipelinePhase("BeforeSetup")
            pipeline.insertPhaseBefore(ApplicationCallPipeline.Setup, beforeSetup)
            val afterCall = PipelinePhase("AfterCall")
            pipeline.insertPhaseAfter(ApplicationCallPipeline.Call, afterCall)

            val koin = pipeline.getKoin()
            val scopeQualifier = named(configuration.scopeName)

            pipeline.intercept(beforeSetup) {
                val callScope = koin.createScope(uniqueScopeId(), scopeQualifier)
                context.attributes.put(callScopeKey, callScope)
            }

            pipeline.intercept(afterCall) {
                context.attributes.getOrNull(callScopeKey)?.close()
                context.attributes.remove(callScopeKey)
            }

            return ApplicationCallKoinScope()
        }

    }
}

private inline fun uniqueScopeId() = UUID.randomUUID().toString()

private val callScopeKey = AttributeKey<Scope>("KoinCallScope")
fun ApplicationCall.getScope() = attributes.getOrNull(callScopeKey)
    ?: throw KoinApplicationCallScopeException("No call scope found. Is KoinApplicationCallScope feature installed?")

inline fun <reified T> ApplicationCall.getCallScoped(): T {
    return getScope().get()
}

class KoinApplicationCallScopeException(msg: String) : Exception(msg)