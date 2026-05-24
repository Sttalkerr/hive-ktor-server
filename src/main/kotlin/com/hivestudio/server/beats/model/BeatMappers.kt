package com.hivestudio.server.beats.model

import com.hivestudio.server.domain.model.Beat

fun Beat.toBeatSummaryResponse(): BeatSummaryResponse =
    BeatSummaryResponse(
        id = id.toString(),
        title = title,
        genre = genre,
        bpm = bpm,
        price = price.toDouble(),
        description = description,
        mp3FileName = mp3FileName,
        coverImageFileName = coverImageFileName,
        createdAt = createdAt.toString(),
    )
