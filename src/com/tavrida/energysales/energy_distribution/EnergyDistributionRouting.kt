package com.tavrida.energysales.energy_distribution

import com.tavrida.utils.di.getCallScoped
import com.tavrida.utils.ktor.respondTo
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable

fun Route.energyDistributionRouting() {
    route("/energy-distribution") {
        post {
            call.receive<MonthOfYearWrapped>()
                .let { energyDistributionService().energyDistribution(it.monthOfYear) }
                .respondTo(call)
        }

        route("/month-range") {
            get {
                energyDistributionService().monthRange()
                    .respondTo(call)
            }
        }

        route("/counter-energy-consumption-details"){
            get{
                energyDistributionService().counterEnergyConsumptionDetails(call.parameters["id"]!!.toInt())
                    .respondTo(call)
            }
        }
    }
}

// Wrap to correctly receive null monthOfYear
@Serializable
private data class MonthOfYearWrapped(val monthOfYear: MonthOfYear?)

private fun PipelineContext<Unit, ApplicationCall>.energyDistributionService() =
    call.getCallScoped<EnergyDistributionService>()