package com.hivestudio.server.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Beat(
    val id: UUID,
    val producerId: UUID,
    val title: String,
    val genre: String,
    val bpm: Int,
    val price: BigDecimal,
    val description: String,
    val mp3FileName: String,
    val coverImageFileName: String,
    val mp3StoragePath: String,
    val coverImageStoragePath: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
