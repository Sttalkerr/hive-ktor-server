package com.hivestudio.server.profile.repository

import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.domain.model.Producer
import com.hivestudio.server.profile.model.UpdateProfileRequest
import java.util.UUID

interface ProducerRepository {
    fun register(request: RegisterRequest): Producer
    fun login(request: LoginRequest): Producer
    fun issueToken(producer: Producer): String
    fun getByToken(token: String): Producer
    fun getById(producerId: UUID): Producer
    fun updateProfile(producerId: UUID, request: UpdateProfileRequest): Producer
    fun updateAvatar(producerId: UUID, avatarFileName: String): Producer
}
