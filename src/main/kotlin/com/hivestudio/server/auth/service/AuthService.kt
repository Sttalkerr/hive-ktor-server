package com.hivestudio.server.auth.service

import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.domain.model.Producer
import com.hivestudio.server.profile.repository.ProducerRepository

class AuthService(
    private val producerRepository: ProducerRepository,
) {
    fun register(request: RegisterRequest): Producer =
        producerRepository.register(request)

    fun login(request: LoginRequest): Producer =
        producerRepository.login(request)
}
