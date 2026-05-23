package com.hivestudio.server.stats.routing

import com.hivestudio.server.stats.model.BeatStatisticsResponse
import com.hivestudio.server.stats.model.SimulationResponse
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.statisticsRoutes() {
    route("/beats/{beatId}") {
        get("/stats") {
            val beatId = call.parameters["beatId"] ?: "demo-beat-id"
            call.respond(
                BeatStatisticsResponse(
                    beatId = beatId,
                    playsCount = 124,
                    likesCount = 37,
                    purchasesCount = 9,
                    revenueTotal = 269.91,
                    updatedAt = "2026-05-24T00:00:00Z",
                )
            )
        }

        route("/simulate") {
            post("/play") {
                call.respond(
                    SimulationResponse(
                        beatId = call.parameters["beatId"] ?: "demo-beat-id",
                        eventType = "play",
                        message = "Play event recorded",
                    )
                )
            }

            post("/like") {
                call.respond(
                    SimulationResponse(
                        beatId = call.parameters["beatId"] ?: "demo-beat-id",
                        eventType = "like",
                        message = "Like event recorded",
                    )
                )
            }

            post("/purchase") {
                call.respond(
                    SimulationResponse(
                        beatId = call.parameters["beatId"] ?: "demo-beat-id",
                        eventType = "purchase",
                        message = "Purchase event recorded",
                    )
                )
            }
        }
    }
}
