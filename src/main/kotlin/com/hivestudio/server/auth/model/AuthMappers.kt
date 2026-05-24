package com.hivestudio.server.auth.model

fun AuthSession.toAuthResponse(): AuthResponse =
    AuthResponse(
        id = id,
        email = email,
        stageName = stageName,
        avatarUrl = avatarUrl,
        token = token,
    )
