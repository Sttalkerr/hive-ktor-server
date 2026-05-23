package com.hivestudio.server.stats.repository

import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatStatistics
import java.util.UUID

class DemoStatisticsRepository(
    private val beatRepository: BeatRepository,
) : StatisticsRepository {
    override fun getStatistics(beatId: UUID): BeatStatistics {
        val beat = beatRepository.getById(beatId)
        return DemoDataFactory.statistics(beat.id)
    }

    override fun recordEvent(beatId: UUID, eventType: BeatEventType): String =
        DemoDataFactory.simulationMessage(beatId, eventType)
}
