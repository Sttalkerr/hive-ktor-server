package com.hivestudio.server.common.model

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val name: String,
    val version: String,
    val status: String,
)
