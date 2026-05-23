package com.hivestudio.server.beats.routing

import com.hivestudio.server.beats.model.BeatSummaryResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.beatRoutes() {
    route("/beats") {
        get {
            call.respond(emptyList<BeatSummaryResponse>())
        }

        get("/{beatId}") {
            call.respond(
                BeatSummaryResponse(
                    id = call.parameters["beatId"] ?: "demo-beat-id",
                    title = "Midnight Pulse",
                    genre = "Trap",
                    bpm = 140,
                    price = 29.99,
                    description = "Dark trap beat for the producer dashboard prototype",
                    mp3FileName = "midnight-pulse.mp3",
                    createdAt = "2026-05-24T00:00:00Z",
                )
            )
        }

        post {
            call.respond(
                status = HttpStatusCode.Created,
                message = BeatSummaryResponse(
                    id = "demo-beat-id",
                    title = "Midnight Pulse",
                    genre = "Trap",
                    bpm = 140,
                    price = 29.99,
                    description = "Dark trap beat for the producer dashboard prototype",
                    mp3FileName = "midnight-pulse.mp3",
                    createdAt = "2026-05-24T00:00:00Z",
                )
            )
        }

        delete("/{beatId}") {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
