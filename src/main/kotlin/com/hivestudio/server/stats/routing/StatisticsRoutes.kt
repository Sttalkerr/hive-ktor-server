package com.hivestudio.server.stats.routing

import com.hivestudio.server.common.auth.requireProducer
import com.hivestudio.server.common.di.AppGraph
import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.stats.model.toHistoryResponse
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
    route("/catalog/beats/{beatId}") {
        get("/stats") {
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: throw IllegalArgumentException("Beat ID is required")
            call.respond(statisticsService.getStatistics(beatId).toStatisticsResponse())
        }

        get("/history") {
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: throw IllegalArgumentException("Beat ID is required")
            val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 7
            call.respond(
                statisticsService.getHistory(beatId, days)
                    .map { it.toHistoryResponse() }
            )
        }
    }

    route("/beats/{beatId}") {
        get("/stats") {
            val producer = call.requireProducer()
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: throw IllegalArgumentException("Beat ID is required")
            call.respond(statisticsService.getStatistics(UUID.fromString(producer.id), beatId).toStatisticsResponse())
        }

        get("/history") {
            val producer = call.requireProducer()
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: throw IllegalArgumentException("Beat ID is required")
            val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 7
            call.respond(
                statisticsService.getHistory(UUID.fromString(producer.id), beatId, days)
                    .map { it.toHistoryResponse() }
            )
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

private suspend fun io.ktor.server.application.ApplicationCall.respondSimulationEvent(
    statisticsService: StatisticsService,
    eventType: BeatEventType,
) {
    val producer = requireProducer()
    val beatId = parameters["beatId"]?.let(UUID::fromString)
        ?: throw IllegalArgumentException("Beat ID is required")
    respond(
        eventType.toSimulationResponse(
            beatId = beatId,
            message = statisticsService.recordEvent(UUID.fromString(producer.id), beatId, eventType),
        )
    )
}
