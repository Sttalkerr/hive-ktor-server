package com.hivestudio.server.stats.repository

import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.domain.model.BeatStatistics
import java.util.UUID

interface StatisticsRepository {
    fun getStatistics(beatId: UUID): BeatStatistics
    fun recordEvent(beatId: UUID, eventType: BeatEventType): String
}
