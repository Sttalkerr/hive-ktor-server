package com.hivestudio.server.demo

import com.hivestudio.server.domain.model.Beat
import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatEvent
import com.hivestudio.server.domain.model.BeatStatistics
import com.hivestudio.server.domain.model.Producer
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

object DemoDataFactory {
    private val producerId: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
    private val firstBeatId: UUID = UUID.fromString("22222222-2222-2222-2222-222222222222")
    private val secondBeatId: UUID = UUID.fromString("33333333-3333-3333-3333-333333333333")
    private val baseTime: Instant = Instant.parse("2026-05-24T00:00:00Z")

    fun producer(): Producer =
        Producer(
            id = producerId,
            email = "producer@hivestudio.dev",
            passwordHash = "demo-password-hash",
            stageName = "Hive Demo",
            bio = "Продюсер электронных и trap-релизов для Hive Studio.",
            city = "Москва",
            contactTag = "@hive_demo",
            avatarFileName = null,
            avatarStoragePath = null,
            createdAt = baseTime,
            updatedAt = baseTime,
        )

    fun beats(): List<Beat> = listOf(
        Beat(
            id = firstBeatId,
            producerId = producerId,
            title = "Midnight Pulse",
            genre = "Trap",
            bpm = 140,
            price = BigDecimal("2990.00"),
            description = "Dark trap beat for the producer dashboard prototype",
            mp3FileName = "midnight-pulse.mp3",
            coverImageFileName = "midnight-pulse-cover.jpg",
            mp3StoragePath = "/uploads/demo/midnight-pulse.mp3",
            coverImageStoragePath = "/uploads/demo/midnight-pulse-cover.jpg",
            createdAt = baseTime,
            updatedAt = baseTime,
        ),
        Beat(
            id = secondBeatId,
            producerId = producerId,
            title = "Velvet Echo",
            genre = "R&B",
            bpm = 96,
            price = BigDecimal("2490.00"),
            description = "Smooth atmospheric beat for melodic vocals",
            mp3FileName = "velvet-echo.mp3",
            coverImageFileName = "velvet-echo-cover.jpg",
            mp3StoragePath = "/uploads/demo/velvet-echo.mp3",
            coverImageStoragePath = "/uploads/demo/velvet-echo-cover.jpg",
            createdAt = baseTime.plusSeconds(7200),
            updatedAt = baseTime.plusSeconds(7200),
        ),
    )

    fun beat(beatId: UUID): Beat = beats().firstOrNull { it.id == beatId } ?: beats().first()

    fun statistics(beatId: UUID): BeatStatistics {
        val beat = beat(beatId)
        val purchases = if (beat.genre == "Trap") 9 else 4
        return BeatStatistics(
            beatId = beat.id,
            playsCount = if (beat.genre == "Trap") 124 else 76,
            likesCount = if (beat.genre == "Trap") 37 else 21,
            purchasesCount = purchases,
            revenueTotal = beat.price.multiply(BigDecimal(purchases)),
            updatedAt = baseTime.plusSeconds(14400),
        )
    }

    fun allStatistics(): Map<UUID, BeatStatistics> =
        beats().associate { beat -> beat.id to statistics(beat.id) }

    fun events(): List<BeatEvent> = buildList {
        addAll(
            buildEventsForBeat(
                beat = beat(firstBeatId),
                dailyPlays = listOf(11, 14, 16, 18, 20, 22, 23),
                dailyLikes = listOf(2, 4, 5, 6, 6, 7, 7),
                dailyPurchases = listOf(1, 1, 1, 2, 1, 1, 2),
            )
        )
        addAll(
            buildEventsForBeat(
                beat = beat(secondBeatId),
                dailyPlays = listOf(8, 9, 10, 11, 12, 13, 13),
                dailyLikes = listOf(2, 2, 3, 3, 3, 4, 4),
                dailyPurchases = listOf(0, 1, 0, 1, 0, 1, 1),
            )
        )
    }

    fun simulationMessage(beatId: UUID, eventType: BeatEventType): String =
        when (eventType) {
            BeatEventType.PLAY -> "Событие прослушивания добавлено для $beatId"
            BeatEventType.LIKE -> "Событие лайка добавлено для $beatId"
            BeatEventType.PURCHASE -> "Событие покупки добавлено для $beatId"
        }

    private fun buildEventsForBeat(
        beat: Beat,
        dailyPlays: List<Int>,
        dailyLikes: List<Int>,
        dailyPurchases: List<Int>,
    ): List<BeatEvent> {
        val points = mutableListOf<BeatEvent>()
        val dayOffsets = 0 until maxOf(dailyPlays.size, dailyLikes.size, dailyPurchases.size)

        dayOffsets.forEach { index ->
            val dayStart = baseTime.minusSeconds(((dayOffsets.last - index) * 86_400L))
            repeat(dailyPlays.getOrElse(index) { 0 }) { playIndex ->
                points += BeatEvent(
                    id = UUID.randomUUID(),
                    beatId = beat.id,
                    eventType = BeatEventType.PLAY,
                    eventValue = BigDecimal.ZERO,
                    createdAt = dayStart.plusSeconds(playIndex.toLong() * 300),
                )
            }
            repeat(dailyLikes.getOrElse(index) { 0 }) { likeIndex ->
                points += BeatEvent(
                    id = UUID.randomUUID(),
                    beatId = beat.id,
                    eventType = BeatEventType.LIKE,
                    eventValue = BigDecimal.ZERO,
                    createdAt = dayStart.plusSeconds(10_000 + likeIndex.toLong() * 600),
                )
            }
            repeat(dailyPurchases.getOrElse(index) { 0 }) { purchaseIndex ->
                points += BeatEvent(
                    id = UUID.randomUUID(),
                    beatId = beat.id,
                    eventType = BeatEventType.PURCHASE,
                    eventValue = beat.price,
                    createdAt = dayStart.plusSeconds(20_000 + purchaseIndex.toLong() * 900),
                )
            }
        }

        return points.sortedBy { it.createdAt }
    }
}
