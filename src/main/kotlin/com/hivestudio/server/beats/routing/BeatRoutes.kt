package com.hivestudio.server.beats.routing

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.beats.model.BeatSummaryResponse
import com.hivestudio.server.beats.model.toBeatSummaryResponse
import com.hivestudio.server.beats.service.BeatService
import com.hivestudio.server.common.di.AppGraph
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.beatRoutes(
    beatService: BeatService = AppGraph.beatService,
) {
    route("/beats") {
        get {
            val query = call.request.queryParameters["query"]
            val beats: List<BeatSummaryResponse> = beatService.getBeats(query).map { it.toBeatSummaryResponse() }
            call.respond(beats)
        }

        get("/{beatId}") {
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: beatService.getBeats(query = null).first().id
            call.respond(beatService.getBeat(beatId).toBeatSummaryResponse())
        }

        post {
            val request = runCatching { call.receive<CreateBeatRequest>() }
                .getOrElse {
                    CreateBeatRequest(
                        title = "Midnight Pulse",
                        genre = "Trap",
                        bpm = 140,
                        price = 29.99,
                        description = "Dark trap beat for the producer dashboard prototype",
                        mp3FileName = "midnight-pulse.mp3",
                        coverImageFileName = "midnight-pulse-cover.jpg",
                    )
                }
            call.respond(
                status = HttpStatusCode.Created,
                message = beatService.createBeat(request).toBeatSummaryResponse(),
            )
        }

        delete("/{beatId}") {
            call.parameters["beatId"]?.let(UUID::fromString)?.let(beatService::deleteBeat)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
