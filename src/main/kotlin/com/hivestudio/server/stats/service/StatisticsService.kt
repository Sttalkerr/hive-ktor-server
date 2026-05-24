package com.hivestudio.server.stats.service

import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatStatistics
import com.hivestudio.server.stats.model.BeatHistoryPoint
import com.hivestudio.server.stats.repository.StatisticsRepository
import java.util.UUID

class StatisticsService(
    private val statisticsRepository: StatisticsRepository,
    private val beatRepository: BeatRepository,
) {
    fun getStatistics(producerId: UUID, beatId: UUID): BeatStatistics {
        beatRepository.getById(producerId, beatId)
        return statisticsRepository.getStatistics(beatId)
    }

    fun recordEvent(producerId: UUID, beatId: UUID, eventType: BeatEventType): String {
        beatRepository.getById(producerId, beatId)
        return statisticsRepository.recordEvent(beatId, eventType)
    }

    fun getHistory(producerId: UUID, beatId: UUID, days: Int = 7): List<BeatHistoryPoint> {
        beatRepository.getById(producerId, beatId)
        return statisticsRepository.getHistory(beatId, days)
    }
}
