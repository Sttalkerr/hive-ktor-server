package com.hivestudio.server.stats.routing

import com.hivestudio.server.common.di.AppGraph
import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.stats.service.StatisticsService
import com.hivestudio.server.stats.model.toSimulationResponse
import com.hivestudio.server.stats.model.toStatisticsResponse
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.statisticsRoutes(
    statisticsService: StatisticsService = AppGraph.statisticsService,
) {
    route("/beats/{beatId}") {
        get("/stats") {
            val beatId = call.parameters["beatId"]?.let(UUID::fromString) ?: DEFAULT_BEAT_ID
            call.respond(statisticsService.getStatistics(beatId).toStatisticsResponse())
        }

        route("/simulate") {
            post("/play") {
                call.respondSimulationEvent(statisticsService, BeatEventType.PLAY)
            }

            post("/like") {
                call.respondSimulationEvent(statisticsService, BeatEventType.LIKE)
            }

            post("/purchase") {
                call.respondSimulationEvent(statisticsService, BeatEventType.PURCHASE)
            }
        }
    }
}

private val DEFAULT_BEAT_ID: UUID = UUID.fromString("22222222-2222-2222-2222-222222222222")

private suspend fun io.ktor.server.application.ApplicationCall.respondSimulationEvent(
    statisticsService: StatisticsService,
    eventType: BeatEventType,
) {
    val beatId = parameters["beatId"]?.let(UUID::fromString) ?: DEFAULT_BEAT_ID
    respond(
        eventType.toSimulationResponse(
            beatId = beatId,
            message = statisticsService.recordEvent(beatId, eventType),
        )
    )
}
