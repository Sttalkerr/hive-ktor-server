package com.hivestudio.server.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BeatStatistics(
    val beatId: UUID,
    val playsCount: Int,
    val likesCount: Int,
    val purchasesCount: Int,
    val revenueTotal: BigDecimal,
    val updatedAt: Instant,
)
