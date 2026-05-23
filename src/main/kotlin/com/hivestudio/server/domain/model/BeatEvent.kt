package com.hivestudio.server.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

enum class BeatEventType {
    PLAY,
    LIKE,
    PURCHASE,
}

data class BeatEvent(
    val id: UUID,
    val beatId: UUID,
    val eventType: BeatEventType,
    val eventValue: BigDecimal,
    val createdAt: Instant,
)
