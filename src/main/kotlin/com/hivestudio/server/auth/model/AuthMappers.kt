package com.hivestudio.server.auth.model

import com.hivestudio.server.domain.model.Producer

fun Producer.toAuthResponse(): AuthResponse =
    AuthResponse(
        id = id.toString(),
        email = email,
        stageName = stageName,
        token = "demo-jwt-token",
    )
