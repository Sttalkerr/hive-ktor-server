package com.hivestudio.server.stats.routing

import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.stats.model.toSimulationResponse
import com.hivestudio.server.stats.model.toStatisticsResponse
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.statisticsRoutes() {
    route("/beats/{beatId}") {
        get("/stats") {
            val beatId = call.parameters["beatId"]?.let(UUID::fromString) ?: DemoDataFactory.beats().first().id
            call.respond(DemoDataFactory.statistics(beatId).toStatisticsResponse())
        }

        route("/simulate") {
            post("/play") {
                call.respondSimulationEvent(BeatEventType.PLAY)
            }

            post("/like") {
                call.respondSimulationEvent(BeatEventType.LIKE)
            }

            post("/purchase") {
                call.respondSimulationEvent(BeatEventType.PURCHASE)
            }
        }
    }
}

private suspend fun io.ktor.server.application.ApplicationCall.respondSimulationEvent(eventType: BeatEventType) {
    val beatId = parameters["beatId"]?.let(UUID::fromString) ?: DemoDataFactory.beats().first().id
    respond(
        eventType.toSimulationResponse(
            beatId = beatId,
            message = DemoDataFactory.simulationMessage(beatId, eventType),
        )
    )
}
