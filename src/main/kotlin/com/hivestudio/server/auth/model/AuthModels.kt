package com.hivestudio.server.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val stageName: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    val id: String,
    val email: String,
    val stageName: String,
    val token: String,
)

data class AuthSession(
    val id: String,
    val email: String,
    val stageName: String,
    val token: String,
)
