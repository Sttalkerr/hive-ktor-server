package com.hivestudio.server.beats.model

import com.hivestudio.server.domain.model.Beat
import com.hivestudio.server.domain.model.Producer

fun Beat.toBeatSummaryResponse(
    producer: Producer,
): BeatSummaryResponse =
    BeatSummaryResponse(
        id = id.toString(),
        title = title,
        genre = genre,
        bpm = bpm,
        price = price.toDouble(),
        description = description,
        producerId = producer.id.toString(),
        producerStageName = producer.stageName,
        producerAvatarUrl = producer.avatarStoragePath,
        mp3FileName = mp3FileName,
        mp3Url = mp3StoragePath,
        coverImageFileName = coverImageFileName,
        coverImageUrl = coverImageStoragePath,
        createdAt = createdAt.toString(),
    )
