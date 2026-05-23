package com.hivestudio.server.beats.repository

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.Beat
import com.hivestudio.server.domain.model.BeatStatistics
import com.hivestudio.server.store.InMemoryHiveStore
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class InMemoryBeatRepository(
    private val store: InMemoryHiveStore,
) : BeatRepository {
    override fun getAll(query: String?): List<Beat> {
        val beats = store.getBeats().sortedByDescending { it.createdAt }
        if (query.isNullOrBlank()) return beats

        return beats.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.genre.contains(query, ignoreCase = true)
        }
    }

    override fun getById(beatId: UUID): Beat =
        store.getBeat(beatId) ?: throw NoSuchElementException("Beat $beatId not found")

    override fun create(request: CreateBeatRequest): Beat {
        val now = Instant.now()
        val beat = Beat(
            id = UUID.randomUUID(),
            producerId = DemoDataFactory.producer().id,
            title = request.title,
            genre = request.genre,
            bpm = request.bpm,
            price = BigDecimal.valueOf(request.price),
            description = request.description,
            mp3FileName = request.mp3FileName,
            mp3StoragePath = "/uploads/demo/${request.mp3FileName}",
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

    override fun delete(beatId: UUID) {
        store.removeBeat(beatId)
    }
}
