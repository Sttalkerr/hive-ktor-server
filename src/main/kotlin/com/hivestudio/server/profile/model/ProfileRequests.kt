package com.hivestudio.server.profile.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val stageName: String,
    val bio: String,
    val city: String,
    val contactTag: String,
)
