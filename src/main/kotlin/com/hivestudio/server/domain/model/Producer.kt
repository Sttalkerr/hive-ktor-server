package com.hivestudio.server.domain.model

import java.time.Instant
import java.util.UUID

data class Producer(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val stageName: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
