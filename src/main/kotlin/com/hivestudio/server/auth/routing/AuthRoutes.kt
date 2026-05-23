package com.hivestudio.server.auth.routing

import com.hivestudio.server.auth.model.toAuthResponse
import com.hivestudio.server.demo.DemoDataFactory
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
                message = DemoDataFactory.producer().toAuthResponse(),
            )
        }

        post("/login") {
            call.respond(DemoDataFactory.producer().toAuthResponse())
        }
    }
}
