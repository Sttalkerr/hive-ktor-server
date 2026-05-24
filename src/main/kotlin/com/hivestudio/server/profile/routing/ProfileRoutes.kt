package com.hivestudio.server.profile.routing

import com.hivestudio.server.common.auth.requireProducer
import com.hivestudio.server.profile.model.toProfileResponse
import com.hivestudio.server.profile.service.ProfileService
import com.hivestudio.server.common.di.AppGraph
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.profileRoutes(
    profileService: ProfileService = AppGraph.profileService,
) {
    get("/profile") {
        val producer = call.requireProducer()
        call.respond(profileService.getProfile(producer.token).toProfileResponse())
    }
}
