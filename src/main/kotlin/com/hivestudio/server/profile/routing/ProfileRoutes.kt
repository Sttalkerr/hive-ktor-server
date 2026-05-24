package com.hivestudio.server.profile.routing

import com.hivestudio.server.common.auth.requireProducer
import com.hivestudio.server.profile.model.UpdateProfileRequest
import com.hivestudio.server.profile.service.ProfileService
import com.hivestudio.server.common.di.AppGraph
import com.hivestudio.server.storage.FileStorageService
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.profileRoutes(
    profileService: ProfileService = AppGraph.profileService,
    fileStorageService: FileStorageService = AppGraph.fileStorageService,
) {
    get("/profile") {
        val producer = call.requireProducer()
        call.respond(profileService.getProfile(producer.token))
    }

    put("/profile") {
        val producer = call.requireProducer()
        val request = call.receive<UpdateProfileRequest>()
        call.respond(profileService.updateProfile(java.util.UUID.fromString(producer.id), request))
    }

    post("/profile/avatar") {
        val producer = call.requireProducer()
        var avatarFileName: String? = null
        call.receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FileItem -> if (part.name == "avatar") {
                    avatarFileName = fileStorageService.save(part, "avatar")
                }
                else -> Unit
            }
            part.dispose()
        }
        call.respond(
            profileService.updateAvatar(
                java.util.UUID.fromString(producer.id),
                avatarFileName ?: throw IllegalArgumentException("Avatar file is required"),
            )
        )
    }
}
