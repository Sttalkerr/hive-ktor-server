package com.hivestudio.server.beats.service

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.domain.model.Beat
import java.util.UUID

class BeatService(
    private val beatRepository: BeatRepository,
) {
    fun getBeats(query: String?): List<Beat> =
        beatRepository.getAll(query)

    fun getBeat(beatId: UUID): Beat =
        beatRepository.getById(beatId)

    fun createBeat(request: CreateBeatRequest): Beat =
        beatRepository.create(request)

    fun deleteBeat(beatId: UUID) {
        beatRepository.getById(beatId)
        beatRepository.delete(beatId)
    }
}
