package com.hivestudio.server.auth.routing

import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.auth.model.toAuthResponse
import com.hivestudio.server.auth.service.AuthService
import com.hivestudio.server.common.di.AppGraph
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(
    authService: AuthService = AppGraph.authService,
) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            call.respond(
                status = HttpStatusCode.Created,
                message = authService.register(request).toAuthResponse(),
            )
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            call.respond(authService.login(request).toAuthResponse())
        }
    }
}
