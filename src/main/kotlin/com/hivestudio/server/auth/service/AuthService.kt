package com.hivestudio.server.auth.service

import com.hivestudio.server.auth.model.AuthSession
import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.domain.model.Producer
import com.hivestudio.server.profile.repository.ProducerRepository

class AuthService(
    private val producerRepository: ProducerRepository,
) {
    fun register(request: RegisterRequest): AuthSession =
        producerRepository.register(request).toSession()

    fun login(request: LoginRequest): AuthSession =
        producerRepository.login(request).toSession()

    fun getByToken(token: String): Producer =
        producerRepository.getByToken(token)

    private fun Producer.toSession(): AuthSession =
        AuthSession(
            id = id.toString(),
            email = email,
            stageName = stageName,
            token = producerRepository.issueToken(this),
        )
}
