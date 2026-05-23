package com.hivestudio.server.beats.repository

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.Beat
import java.util.UUID

class DemoBeatRepository : BeatRepository {
    override fun getAll(query: String?): List<Beat> {
        val beats = DemoDataFactory.beats()
        if (query.isNullOrBlank()) return beats

        return beats.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.genre.contains(query, ignoreCase = true)
        }
    }

    override fun getById(beatId: UUID): Beat = DemoDataFactory.beat(beatId)

    override fun create(request: CreateBeatRequest): Beat = DemoDataFactory.beats().first()

    override fun delete(beatId: UUID) = Unit
}
