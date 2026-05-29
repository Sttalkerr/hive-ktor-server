package com.hivestudio.server.beats.routing

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.beats.model.BeatSummaryResponse
import com.hivestudio.server.beats.model.UpdateBeatRequest
import com.hivestudio.server.beats.model.toBeatSummaryResponse
import com.hivestudio.server.beats.service.BeatService
import com.hivestudio.server.common.auth.requireProducer
import com.hivestudio.server.common.di.AppGraph
import com.hivestudio.server.stats.repository.StatisticsRepository
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
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.util.UUID

fun Route.beatRoutes(
    beatService: BeatService = AppGraph.beatService,
    statisticsRepository: StatisticsRepository = AppGraph.statisticsRepository,
    fileStorageService: FileStorageService = AppGraph.fileStorageService,
) {
    route("/catalog/beats") {
        get {
            val query = call.request.queryParameters["query"]
            val beats: List<BeatSummaryResponse> = beatService
                .getCatalogBeats(query)
                .map { (beat, producer) ->
                    beat.toBeatSummaryResponse(producer, statisticsRepository.getStatistics(beat.id))
                }
            call.respond(beats)
        }

        get("/{beatId}") {
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: throw IllegalArgumentException("Beat ID is required")
            val (beat, producer) = beatService.getCatalogBeat(beatId)
            call.respond(beat.toBeatSummaryResponse(producer, statisticsRepository.getStatistics(beat.id)))
        }
    }

    route("/beats") {
        get {
            val producer = call.requireProducer()
            val query = call.request.queryParameters["query"]
            val beats: List<BeatSummaryResponse> = beatService
                .getBeats(UUID.fromString(producer.id), query)
                .map { (beat, owner) ->
                    beat.toBeatSummaryResponse(owner, statisticsRepository.getStatistics(beat.id))
                }
            call.respond(beats)
        }

        get("/{beatId}") {
            val producer = call.requireProducer()
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: throw IllegalArgumentException("Beat ID is required")
            val (beat, owner) = beatService.getBeat(UUID.fromString(producer.id), beatId)
            call.respond(beat.toBeatSummaryResponse(owner, statisticsRepository.getStatistics(beat.id)))
        }

        post {
            val producer = call.requireProducer()
            val request = call.parseCreateBeatRequest(fileStorageService)
            val createdBeat = beatService.createBeat(UUID.fromString(producer.id), request)
            call.respond(
                status = HttpStatusCode.Created,
                message = createdBeat
                    .toBeatSummaryResponse(
                        AppGraph.producerRepository.getById(UUID.fromString(producer.id)),
                        statisticsRepository.getStatistics(createdBeat.id),
                    ),
            )
        }

        put("/{beatId}") {
            val producer = call.requireProducer()
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: throw IllegalArgumentException("Beat ID is required")
            val request = call.receive<UpdateBeatRequest>()
            val updatedBeat = beatService.updateBeat(UUID.fromString(producer.id), beatId, request)
            call.respond(
                updatedBeat.toBeatSummaryResponse(
                    AppGraph.producerRepository.getById(UUID.fromString(producer.id)),
                    statisticsRepository.getStatistics(updatedBeat.id),
                )
            )
        }

        delete("/{beatId}") {
            val producer = call.requireProducer()
            val beatId = call.parameters["beatId"]?.let(UUID::fromString)
                ?: throw IllegalArgumentException("Beat ID is required")
            beatService.deleteBeat(UUID.fromString(producer.id), beatId)
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
