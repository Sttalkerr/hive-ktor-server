package com.hivestudio.server.domain.model

import java.time.Instant
import java.util.UUID

data class Producer(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val stageName: String,
    val bio: String,
    val city: String,
    val contactTag: String,
    val avatarFileName: String?,
    val avatarStoragePath: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
