package com.hivestudio.server.database.repository

import com.hivestudio.server.database.config.DatabaseFactory
import com.hivestudio.server.database.schema.BeatEventsTable
import com.hivestudio.server.database.schema.BeatStatisticsTable
import com.hivestudio.server.database.schema.BeatsTable
import com.hivestudio.server.domain.model.BeatEvent
import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatStatistics
import org.jetbrains.exposed.dao.id.EntityID
import com.hivestudio.server.stats.model.BeatHistoryPoint
import com.hivestudio.server.stats.repository.StatisticsRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class PostgresStatisticsRepository : StatisticsRepository {
    override fun getStatistics(beatId: UUID): BeatStatistics = DatabaseFactory.query {
        findStatistics(beatId)
    }

    override fun recordEvent(beatId: UUID, eventType: BeatEventType): String = DatabaseFactory.query {
        val current = findStatistics(beatId)
        val beatPrice = BeatsTable
            .selectAll()
            .firstOrNull { it[BeatsTable.id].value == beatId }
            ?.get(BeatsTable.price)
            ?: throw NoSuchElementException("Beat $beatId not found")

        val now = Instant.now()
        val revenueDelta = if (eventType == BeatEventType.PURCHASE) beatPrice else BigDecimal.ZERO
        BeatStatisticsTable.update({ BeatStatisticsTable.beatId eq beatId }) {
            it[playsCount] = current.playsCount + if (eventType == BeatEventType.PLAY) 1 else 0
            it[likesCount] = current.likesCount + if (eventType == BeatEventType.LIKE) 1 else 0
            it[purchasesCount] = current.purchasesCount + if (eventType == BeatEventType.PURCHASE) 1 else 0
            it[revenueTotal] = current.revenueTotal + revenueDelta
            it[updatedAt] = now.toOffsetUtc()
        }
        BeatEventsTable.insert {
            it[id] = EntityID(UUID.randomUUID(), BeatEventsTable)
            it[BeatEventsTable.beatId] = EntityID(beatId, BeatsTable)
            it[BeatEventsTable.eventType] = eventType.name
            it[eventValue] = revenueDelta
            it[createdAt] = now.toOffsetUtc()
        }
        when (eventType) {
            BeatEventType.PLAY -> "Событие прослушивания добавлено для $beatId"
            BeatEventType.LIKE -> "Событие лайка добавлено для $beatId"
            BeatEventType.PURCHASE -> "Событие покупки добавлено для $beatId"
        }
    }

    override fun getHistory(beatId: UUID, days: Int): List<BeatHistoryPoint> = DatabaseFactory.query {
        val statistics = findStatistics(beatId)
        val events = BeatEventsTable
            .selectAll()
            .filter { it[BeatEventsTable.beatId].value == beatId }
            .map(::toBeatEvent)
            .sortedBy { it.createdAt }

        val anchorDate = (events.maxByOrNull { it.createdAt }?.createdAt ?: statistics.updatedAt)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        val grouped = events.groupBy { it.createdAt.atZone(ZoneOffset.UTC).toLocalDate() }

        (days - 1 downTo 0).map { offset ->
            val date = anchorDate.minusDays(offset.toLong())
            val dayEvents = grouped[date].orEmpty()
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

    private fun findStatistics(beatId: UUID): BeatStatistics =
        BeatStatisticsTable
            .selectAll()
            .firstOrNull { it[BeatStatisticsTable.beatId].value == beatId }
            ?.let(::toBeatStatistics)
            ?: throw NoSuchElementException("Statistics for beat $beatId not found")
}

private fun toBeatStatistics(row: ResultRow): BeatStatistics =
    BeatStatistics(
        beatId = row[BeatStatisticsTable.beatId].value,
        playsCount = row[BeatStatisticsTable.playsCount],
        likesCount = row[BeatStatisticsTable.likesCount],
        purchasesCount = row[BeatStatisticsTable.purchasesCount],
        revenueTotal = row[BeatStatisticsTable.revenueTotal],
        updatedAt = row[BeatStatisticsTable.updatedAt].toInstant(),
    )

private fun toBeatEvent(row: ResultRow): BeatEvent =
    BeatEvent(
        id = row[BeatEventsTable.id].value,
        beatId = row[BeatEventsTable.beatId].value,
        eventType = BeatEventType.valueOf(row[BeatEventsTable.eventType]),
        eventValue = row[BeatEventsTable.eventValue],
        createdAt = row[BeatEventsTable.createdAt].toInstant(),
    )

private fun Instant.toOffsetUtc(): OffsetDateTime = OffsetDateTime.ofInstant(this, ZoneOffset.UTC)
