package com.hivestudio.server.auth.routing

import com.hivestudio.server.auth.model.AuthResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() {
    route("/auth") {
        post("/register") {
            call.respond(
                status = HttpStatusCode.Created,
                message = AuthResponse(
                    id = "demo-producer-id",
                    email = "producer@hivestudio.dev",
                    stageName = "Hive Demo",
                    token = "demo-jwt-token",
                )
            )
        }

        post("/login") {
            call.respond(
                AuthResponse(
                    id = "demo-producer-id",
                    email = "producer@hivestudio.dev",
                    stageName = "Hive Demo",
                    token = "demo-jwt-token",
                )
            )
        }
    }
}
