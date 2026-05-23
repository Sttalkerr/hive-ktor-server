package com.hivestudio.server.profile.routing

import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.profile.model.toProfileResponse
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.profileRoutes() {
    get("/profile") {
        call.respond(DemoDataFactory.producer().toProfileResponse())
    }
}
