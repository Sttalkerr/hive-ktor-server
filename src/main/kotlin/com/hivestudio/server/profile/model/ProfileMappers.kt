package com.hivestudio.server.profile.model

import com.hivestudio.server.domain.model.Producer

fun Producer.toProfileResponse(
    beatsCount: Int,
    totalPlays: Int,
    totalRevenue: Double,
): ProfileResponse =
    ProfileResponse(
        id = id.toString(),
        email = email,
        stageName = stageName,
        bio = bio,
        city = city,
        contactTag = contactTag,
        avatarUrl = avatarStoragePath,
        beatsCount = beatsCount,
        totalPlays = totalPlays,
        totalRevenue = totalRevenue,
        createdAt = createdAt.toString(),
    )
