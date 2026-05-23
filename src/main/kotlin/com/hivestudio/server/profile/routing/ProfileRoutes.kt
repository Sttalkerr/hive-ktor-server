package com.hivestudio.server.profile.routing

import com.hivestudio.server.profile.model.ProfileResponse
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.profileRoutes() {
    get("/profile") {
        call.respond(
            ProfileResponse(
                id = "demo-producer-id",
                email = "producer@hivestudio.dev",
                stageName = "Hive Demo",
                createdAt = "2026-05-24T00:00:00Z",
            )
        )
    }
}
