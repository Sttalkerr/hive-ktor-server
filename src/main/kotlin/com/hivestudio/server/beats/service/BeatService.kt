package com.hivestudio.server.beats.service

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.domain.model.Beat
import java.util.UUID

class BeatService(
    private val beatRepository: BeatRepository,
) {
    fun getBeats(producerId: UUID, query: String?): List<Beat> =
        beatRepository.getAll(producerId, query)

    fun getBeat(producerId: UUID, beatId: UUID): Beat =
        beatRepository.getById(producerId, beatId)

    fun createBeat(producerId: UUID, request: CreateBeatRequest): Beat =
        beatRepository.create(producerId, request)

    fun deleteBeat(producerId: UUID, beatId: UUID) {
        beatRepository.getById(producerId, beatId)
        beatRepository.delete(producerId, beatId)
    }
}
