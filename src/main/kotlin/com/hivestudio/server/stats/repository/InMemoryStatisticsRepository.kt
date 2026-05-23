package com.hivestudio.server.stats.repository

import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatStatistics
import com.hivestudio.server.store.InMemoryHiveStore
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class InMemoryStatisticsRepository(
    private val store: InMemoryHiveStore,
) : StatisticsRepository {
    override fun getStatistics(beatId: UUID): BeatStatistics =
        store.getStatistics(beatId) ?: throw NoSuchElementException("Statistics for beat $beatId not found")

    override fun recordEvent(beatId: UUID, eventType: BeatEventType): String {
        val current = getStatistics(beatId)
        val beat = store.getBeat(beatId) ?: throw NoSuchElementException("Beat $beatId not found")
        val updated = when (eventType) {
            BeatEventType.PLAY -> current.copy(
                playsCount = current.playsCount + 1,
                updatedAt = Instant.now(),
            )
            BeatEventType.LIKE -> current.copy(
                likesCount = current.likesCount + 1,
                updatedAt = Instant.now(),
            )
            BeatEventType.PURCHASE -> current.copy(
                purchasesCount = current.purchasesCount + 1,
                revenueTotal = current.revenueTotal.add(beat.price),
                updatedAt = Instant.now(),
            )
        }
        store.putStatistics(updated)
        return when (eventType) {
            BeatEventType.PLAY -> "Событие прослушивания добавлено для $beatId"
            BeatEventType.LIKE -> "Событие лайка добавлено для $beatId"
            BeatEventType.PURCHASE -> "Событие покупки добавлено для $beatId"
        }
    }
}
