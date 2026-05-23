package com.hivestudio.server.stats.model

import kotlinx.serialization.Serializable

@Serializable
data class BeatStatisticsResponse(
    val beatId: String,
    val playsCount: Int,
    val likesCount: Int,
    val purchasesCount: Int,
    val revenueTotal: Double,
    val updatedAt: String,
)

@Serializable
data class SimulationResponse(
    val beatId: String,
    val eventType: String,
    val message: String,
)
