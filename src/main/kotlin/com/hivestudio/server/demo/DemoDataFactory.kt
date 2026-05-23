package com.hivestudio.server.demo

import com.hivestudio.server.domain.model.Beat
import com.hivestudio.server.domain.model.BeatEventType
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
            price = BigDecimal("29.99"),
            description = "Dark trap beat for the producer dashboard prototype",
            mp3FileName = "midnight-pulse.mp3",
            mp3StoragePath = "/uploads/demo/midnight-pulse.mp3",
            createdAt = baseTime,
            updatedAt = baseTime,
        ),
        Beat(
            id = secondBeatId,
            producerId = producerId,
            title = "Velvet Echo",
            genre = "R&B",
            bpm = 96,
            price = BigDecimal("24.99"),
            description = "Smooth atmospheric beat for melodic vocals",
            mp3FileName = "velvet-echo.mp3",
            mp3StoragePath = "/uploads/demo/velvet-echo.mp3",
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

    fun simulationMessage(beatId: UUID, eventType: BeatEventType): String =
        when (eventType) {
            BeatEventType.PLAY -> "Событие прослушивания добавлено для $beatId"
            BeatEventType.LIKE -> "Событие лайка добавлено для $beatId"
            BeatEventType.PURCHASE -> "Событие покупки добавлено для $beatId"
        }
}
