package com.hivestudio.server.profile.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: String,
    val email: String,
    val stageName: String,
    val bio: String,
    val city: String,
    val contactTag: String,
    val avatarUrl: String?,
    val beatsCount: Int,
    val totalPlays: Int,
    val totalRevenue: Double,
    val createdAt: String,
)
