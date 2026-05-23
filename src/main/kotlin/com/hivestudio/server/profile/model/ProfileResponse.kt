package com.hivestudio.server.profile.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: String,
    val email: String,
    val stageName: String,
    val createdAt: String,
)
