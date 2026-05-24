package com.hivestudio.server.profile.service

import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.profile.model.ProfileResponse
import com.hivestudio.server.profile.model.UpdateProfileRequest
import com.hivestudio.server.profile.model.toProfileResponse
import com.hivestudio.server.profile.repository.ProducerRepository
import com.hivestudio.server.stats.repository.StatisticsRepository
import java.util.UUID

class ProfileService(
    private val producerRepository: ProducerRepository,
    private val beatRepository: BeatRepository,
    private val statisticsRepository: StatisticsRepository,
) {
    fun getProfile(token: String): ProfileResponse {
        val producer = producerRepository.getByToken(token)
        return buildProfileResponse(producer.id)
    }

    fun updateProfile(producerId: UUID, request: UpdateProfileRequest): ProfileResponse {
        producerRepository.updateProfile(producerId, request)
        return buildProfileResponse(producerId)
    }

    fun updateAvatar(producerId: UUID, avatarFileName: String): ProfileResponse {
        producerRepository.updateAvatar(producerId, avatarFileName)
        return buildProfileResponse(producerId)
    }

    private fun buildProfileResponse(producerId: UUID): ProfileResponse {
        val producer = producerRepository.getById(producerId)
        val beats = beatRepository.getAll(producerId)
        val statistics = beats.mapNotNull { runCatching { statisticsRepository.getStatistics(it.id) }.getOrNull() }

        return producer.toProfileResponse(
            beatsCount = beats.size,
            totalPlays = statistics.sumOf { it.playsCount },
            totalRevenue = statistics.sumOf { it.revenueTotal.toDouble() },
        )
    }
}
