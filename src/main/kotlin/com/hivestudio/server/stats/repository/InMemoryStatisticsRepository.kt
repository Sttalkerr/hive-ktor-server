package com.hivestudio.server.stats.repository

import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatEvent
import com.hivestudio.server.domain.model.BeatStatistics
import com.hivestudio.server.stats.model.BeatHistoryPoint
import com.hivestudio.server.store.InMemoryHiveStore
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

class InMemoryStatisticsRepository(
    private val store: InMemoryHiveStore,
) : StatisticsRepository {
    override fun getStatistics(beatId: UUID): BeatStatistics =
        store.getStatistics(beatId) ?: throw NoSuchElementException("Statistics for beat $beatId not found")

    override fun recordEvent(beatId: UUID, eventType: BeatEventType): String {
        val current = getStatistics(beatId)
        val beat = store.getBeat(beatId) ?: throw NoSuchElementException("Beat $beatId not found")
        val now = Instant.now()
        val updated = when (eventType) {
            BeatEventType.PLAY -> current.copy(
                playsCount = current.playsCount + 1,
                updatedAt = now,
            )
            BeatEventType.LIKE -> current.copy(
                likesCount = current.likesCount + 1,
                updatedAt = now,
            )
            BeatEventType.PURCHASE -> current.copy(
                purchasesCount = current.purchasesCount + 1,
                revenueTotal = current.revenueTotal.add(beat.price),
                updatedAt = now,
            )
        }
        store.putStatistics(updated)
        store.putEvent(
            BeatEvent(
                id = UUID.randomUUID(),
                beatId = beatId,
                eventType = eventType,
                eventValue = if (eventType == BeatEventType.PURCHASE) beat.price else BigDecimal.ZERO,
                createdAt = now,
            )
        )
        return when (eventType) {
            BeatEventType.PLAY -> "Событие прослушивания добавлено для $beatId"
            BeatEventType.LIKE -> "Событие лайка добавлено для $beatId"
            BeatEventType.PURCHASE -> "Событие покупки добавлено для $beatId"
        }
    }

    override fun getHistory(beatId: UUID, days: Int): List<BeatHistoryPoint> {
        val statistics = getStatistics(beatId)
        val events = store.getEvents(beatId)
        val anchorDate = (events.maxByOrNull { it.createdAt }?.createdAt ?: statistics.updatedAt)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val groupedEvents = events.groupBy { it.createdAt.atZone(ZoneOffset.UTC).toLocalDate() }

        return (days - 1 downTo 0).map { offset ->
            val date = anchorDate.minusDays(offset.toLong())
            val dayEvents = groupedEvents[date].orEmpty()
            BeatHistoryPoint(
                date = date.toString(),
                playsCount = dayEvents.count { it.eventType == BeatEventType.PLAY },
                likesCount = dayEvents.count { it.eventType == BeatEventType.LIKE },
                purchasesCount = dayEvents.count { it.eventType == BeatEventType.PURCHASE },
                revenueTotal = dayEvents
                    .filter { it.eventType == BeatEventType.PURCHASE }
                    .fold(BigDecimal.ZERO) { total, event -> total + event.eventValue }
                    .toDouble(),
            )
        }
    }
}
