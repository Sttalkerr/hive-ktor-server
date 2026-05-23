package com.hivestudio.server.stats.model

import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatStatistics
import java.util.UUID

fun BeatStatistics.toStatisticsResponse(): BeatStatisticsResponse =
    BeatStatisticsResponse(
        beatId = beatId.toString(),
        playsCount = playsCount,
        likesCount = likesCount,
        purchasesCount = purchasesCount,
        revenueTotal = revenueTotal.toDouble(),
        updatedAt = updatedAt.toString(),
    )

fun BeatEventType.toSimulationResponse(beatId: UUID, message: String): SimulationResponse =
    SimulationResponse(
        beatId = beatId.toString(),
        eventType = name.lowercase(),
        message = message,
    )
