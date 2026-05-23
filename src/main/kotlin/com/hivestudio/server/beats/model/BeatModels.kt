package com.hivestudio.server.beats.model

import kotlinx.serialization.Serializable

@Serializable
data class BeatSummaryResponse(
    val id: String,
    val title: String,
    val genre: String,
    val bpm: Int,
    val price: Double,
    val description: String,
    val mp3FileName: String,
    val createdAt: String,
)

@Serializable
data class CreateBeatRequest(
    val title: String,
    val genre: String,
    val bpm: Int,
    val price: Double,
    val description: String,
    val mp3FileName: String,
)
