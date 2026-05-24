package com.hivestudio.server.profile.repository

import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.domain.model.Producer

interface ProducerRepository {
    fun register(request: RegisterRequest): Producer
    fun login(request: LoginRequest): Producer
    fun issueToken(producer: Producer): String
    fun getByToken(token: String): Producer
}
