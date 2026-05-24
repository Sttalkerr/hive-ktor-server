package com.hivestudio.server.beats.routing

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.beats.model.BeatSummaryResponse
import com.hivestudio.server.beats.model.toBeatSummaryResponse
import com.hivestudio.server.beats.service.BeatService
import com.hivestudio.server.common.di.AppGraph
import com.hivestudio.server.storage.FileStorageService
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.contentType
import io.ktor.server.request.receiveMultipart
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
    fileStorageService: FileStorageService = AppGraph.fileStorageService,
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
            val request = call.parseCreateBeatRequest(fileStorageService)
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

private suspend fun io.ktor.server.application.ApplicationCall.parseCreateBeatRequest(
    fileStorageService: FileStorageService,
): CreateBeatRequest {
    val contentType = request.contentType().withoutParameters()

    if (contentType == ContentType.MultiPart.FormData) {
        val formValues = mutableMapOf<String, String>()
        var mp3FileName: String? = null
        var coverImageFileName: String? = null

        receiveMultipart().forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    formValues[part.name ?: ""] = part.value
                }
                is PartData.FileItem -> {
                    when (part.name) {
                        "mp3" -> mp3FileName = fileStorageService.save(part, "beat")
                        "coverImage" -> coverImageFileName = fileStorageService.save(part, "cover")
                    }
                }
                else -> Unit
            }
            part.dispose()
        }

        return CreateBeatRequest(
            title = formValues.required("title"),
            genre = formValues.required("genre"),
            bpm = formValues.required("bpm").toInt(),
            price = formValues.required("price").toDouble(),
            description = formValues.required("description"),
            mp3FileName = mp3FileName ?: throw IllegalArgumentException("MP3 file is required"),
            coverImageFileName = coverImageFileName ?: throw IllegalArgumentException("Cover image is required"),
        )
    }

    if (contentType.match(ContentType.Application.Json)) {
        return receive<CreateBeatRequest>()
    }

    throw IllegalArgumentException("Unsupported content type for beat upload: $contentType")
}

private fun Map<String, String>.required(name: String): String =
    this[name]?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("Field $name is required")
