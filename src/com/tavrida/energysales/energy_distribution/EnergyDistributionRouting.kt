package com.tavrida.energysales.energy_distribution

import com.tavrida.utils.di.getCallScoped
import com.tavrida.utils.ktor.respondTo
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

fun Route.energyDistributionRouting() {
    route("/energy-distribution") {
        post {
            call.receiveOrNull<MonthOfYear>()
                .let { energyDistributionService().energyDistribution(it) }
                .respondTo(call)
        }

        route("/month-range") {
            get {
                energyDistributionService().monthRange()
                    .respondTo(call)
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.energyDistributionService() =
    call.getCallScoped<EnergyDistributionService>()