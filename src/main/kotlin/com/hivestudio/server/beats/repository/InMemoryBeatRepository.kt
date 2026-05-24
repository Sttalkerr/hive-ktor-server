package com.hivestudio.server.beats.repository

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.domain.model.Beat
import com.hivestudio.server.domain.model.BeatStatistics
import com.hivestudio.server.store.InMemoryHiveStore
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class InMemoryBeatRepository(
    private val store: InMemoryHiveStore,
) : BeatRepository {
    override fun getAll(producerId: UUID, query: String?): List<Beat> {
        val beats = store.getBeats().filter { it.producerId == producerId }
        return filterBeats(beats, query)
    }

    override fun getAllPublic(query: String?): List<Beat> =
        filterBeats(store.getBeats(), query)

    override fun getPublicById(beatId: UUID): Beat =
        store.getBeat(beatId) ?: throw NoSuchElementException("Beat $beatId not found")

    override fun getById(producerId: UUID, beatId: UUID): Beat {
        val beat = store.getBeat(beatId) ?: throw NoSuchElementException("Beat $beatId not found")
        if (beat.producerId != producerId) {
            throw NoSuchElementException("Beat $beatId not found")
        }
        return beat
    }

    override fun create(producerId: UUID, request: CreateBeatRequest): Beat {
        val now = Instant.now()
        val beat = Beat(
            id = UUID.randomUUID(),
            producerId = producerId,
            title = request.title,
            genre = request.genre,
            bpm = request.bpm,
            price = BigDecimal.valueOf(request.price),
            description = request.description,
            mp3FileName = request.mp3FileName,
            coverImageFileName = request.coverImageFileName,
            mp3StoragePath = "/uploads/${request.mp3FileName}",
            coverImageStoragePath = "/uploads/${request.coverImageFileName}",
            createdAt = now,
            updatedAt = now,
        )
        store.putBeat(beat)
        store.putStatistics(
            BeatStatistics(
                beatId = beat.id,
                playsCount = 0,
                likesCount = 0,
                purchasesCount = 0,
                revenueTotal = BigDecimal.ZERO,
                updatedAt = now,
            )
        )
        return beat
    }

    override fun delete(producerId: UUID, beatId: UUID) {
        getById(producerId, beatId)
        store.removeBeat(beatId)
    }

    private fun filterBeats(beats: List<Beat>, query: String?): List<Beat> {
        val sorted = beats.sortedByDescending { it.createdAt }
        if (query.isNullOrBlank()) return sorted

        return sorted.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.genre.contains(query, ignoreCase = true)
        }
    }
}
