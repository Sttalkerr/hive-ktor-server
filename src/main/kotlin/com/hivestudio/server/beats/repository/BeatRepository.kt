package com.hivestudio.server.beats.repository

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.domain.model.Beat
import java.util.UUID

interface BeatRepository {
    fun getAll(query: String? = null): List<Beat>
    fun getById(beatId: UUID): Beat
    fun create(request: CreateBeatRequest): Beat
    fun delete(beatId: UUID)
}
