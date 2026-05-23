package com.hivestudio.server.stats.service

import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatStatistics
import com.hivestudio.server.stats.repository.StatisticsRepository
import java.util.UUID

class StatisticsService(
    private val statisticsRepository: StatisticsRepository,
) {
    fun getStatistics(beatId: UUID): BeatStatistics =
        statisticsRepository.getStatistics(beatId)

    fun recordEvent(beatId: UUID, eventType: BeatEventType): String =
        statisticsRepository.recordEvent(beatId, eventType)
}
