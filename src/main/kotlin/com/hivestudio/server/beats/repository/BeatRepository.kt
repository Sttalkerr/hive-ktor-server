package com.hivestudio.server.beats.repository

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.domain.model.Beat
import java.util.UUID

interface BeatRepository {
    fun getAll(producerId: UUID, query: String? = null): List<Beat>
    fun getAllPublic(query: String? = null): List<Beat>
    fun getPublicById(beatId: UUID): Beat
    fun getById(producerId: UUID, beatId: UUID): Beat
    fun create(producerId: UUID, request: CreateBeatRequest): Beat
    fun delete(producerId: UUID, beatId: UUID)
}
