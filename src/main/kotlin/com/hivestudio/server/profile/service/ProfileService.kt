package com.hivestudio.server.profile.service

import com.hivestudio.server.domain.model.Producer
import com.hivestudio.server.profile.repository.ProducerRepository

class ProfileService(
    private val producerRepository: ProducerRepository,
) {
    fun getProfile(): Producer =
        producerRepository.getCurrent()
}
