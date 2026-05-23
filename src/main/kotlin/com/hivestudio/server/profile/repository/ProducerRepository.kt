package com.hivestudio.server.profile.repository

import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.domain.model.Producer

interface ProducerRepository {
    fun getCurrent(): Producer
    fun register(request: RegisterRequest): Producer
    fun login(request: LoginRequest): Producer
}
