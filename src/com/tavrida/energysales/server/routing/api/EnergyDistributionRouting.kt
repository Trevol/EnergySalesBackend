package com.tavrida.energysales.server.routing.api

import com.tavrida.energysales.energy_distribution.EnergyDistributionService
import com.tavrida.energysales.energy_distribution.MonthOfYear
import com.tavrida.utils.di.getCallScoped
import com.tavrida.utils.ktor.respondTo
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlin.text.get

fun Route.energyDistributionRouting() {
    route("/energy-distribution") {
        post {
            call.receiveOrNull<MonthOfYear>()
                .let { energyDistributionService().getEnergyDistribution(it) }
                .respondTo(call)
        }

        route("/month-range") {
            get {
                energyDistributionService().getMonthRange()
                    .respondTo(call)
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.energyDistributionService() =
    call.getCallScoped<EnergyDistributionService>()