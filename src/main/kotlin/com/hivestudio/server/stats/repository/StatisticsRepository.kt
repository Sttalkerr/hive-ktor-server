package com.hivestudio.server.stats.repository

import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatStatistics
import com.hivestudio.server.stats.model.BeatHistoryPoint
import java.util.UUID

interface StatisticsRepository {
    fun getStatistics(beatId: UUID): BeatStatistics
    fun recordEvent(beatId: UUID, eventType: BeatEventType): String
    fun getHistory(beatId: UUID, days: Int = 7): List<BeatHistoryPoint>
}
