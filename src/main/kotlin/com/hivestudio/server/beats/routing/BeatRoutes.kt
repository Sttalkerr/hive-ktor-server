package com.hivestudio.server.beats.routing

import com.hivestudio.server.beats.model.BeatSummaryResponse
import com.hivestudio.server.beats.model.toBeatSummaryResponse
import com.hivestudio.server.demo.DemoDataFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.beatRoutes() {
    route("/beats") {
        get {
            val beats: List<BeatSummaryResponse> = DemoDataFactory.beats().map { it.toBeatSummaryResponse() }
            call.respond(beats)
        }

        get("/{beatId}") {
            val beatId = call.parameters["beatId"]?.let(UUID::fromString) ?: DemoDataFactory.beats().first().id
            call.respond(DemoDataFactory.beat(beatId).toBeatSummaryResponse())
        }

        post {
            call.respond(
                status = HttpStatusCode.Created,
                message = DemoDataFactory.beats().first().toBeatSummaryResponse(),
            )
        }

        delete("/{beatId}") {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
