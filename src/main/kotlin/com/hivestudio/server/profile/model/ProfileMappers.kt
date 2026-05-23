package com.hivestudio.server.profile.model

import com.hivestudio.server.domain.model.Producer

fun Producer.toProfileResponse(): ProfileResponse =
    ProfileResponse(
        id = id.toString(),
        email = email,
        stageName = stageName,
        createdAt = createdAt.toString(),
    )
