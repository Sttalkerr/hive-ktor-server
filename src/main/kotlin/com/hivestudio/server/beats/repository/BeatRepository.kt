package com.hivestudio.server.beats.repository

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.domain.model.Beat
import java.util.UUID

interface BeatRepository {
    fun getAll(producerId: UUID, query: String? = null): List<Beat>
    fun getById(producerId: UUID, beatId: UUID): Beat
    fun create(producerId: UUID, request: CreateBeatRequest): Beat
    fun delete(producerId: UUID, beatId: UUID)
}
