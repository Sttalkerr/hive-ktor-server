package com.hivestudio.server.beats.service

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.domain.model.Beat
import com.hivestudio.server.domain.model.Producer
import com.hivestudio.server.profile.repository.ProducerRepository
import java.util.UUID

class BeatService(
    private val beatRepository: BeatRepository,
    private val producerRepository: ProducerRepository,
) {
    fun getBeats(producerId: UUID, query: String?): List<Pair<Beat, Producer>> =
        beatRepository.getAll(producerId, query).map { it to producerRepository.getById(it.producerId) }

    fun getCatalogBeats(query: String?): List<Pair<Beat, Producer>> =
        beatRepository.getAllPublic(query).map { it to producerRepository.getById(it.producerId) }

    fun getCatalogBeat(beatId: UUID): Pair<Beat, Producer> {
        val beat = beatRepository.getPublicById(beatId)
        return beat to producerRepository.getById(beat.producerId)
    }

    fun getBeat(producerId: UUID, beatId: UUID): Pair<Beat, Producer> {
        val beat = beatRepository.getById(producerId, beatId)
        return beat to producerRepository.getById(beat.producerId)
    }

    fun createBeat(producerId: UUID, request: CreateBeatRequest): Beat =
        beatRepository.create(producerId, request)

    fun deleteBeat(producerId: UUID, beatId: UUID) {
        beatRepository.getById(producerId, beatId)
        beatRepository.delete(producerId, beatId)
    }
}
