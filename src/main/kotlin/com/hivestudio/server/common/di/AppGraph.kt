package com.hivestudio.server.common.di

import com.hivestudio.server.auth.service.AuthService
import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.beats.repository.DemoBeatRepository
import com.hivestudio.server.beats.service.BeatService
import com.hivestudio.server.profile.repository.DemoProducerRepository
import com.hivestudio.server.profile.repository.ProducerRepository
import com.hivestudio.server.profile.service.ProfileService
import com.hivestudio.server.stats.repository.DemoStatisticsRepository
import com.hivestudio.server.stats.repository.StatisticsRepository
import com.hivestudio.server.stats.service.StatisticsService

object AppGraph {
    val producerRepository: ProducerRepository = DemoProducerRepository()
    val beatRepository: BeatRepository = DemoBeatRepository()
    val statisticsRepository: StatisticsRepository = DemoStatisticsRepository(beatRepository)

    val authService: AuthService = AuthService(producerRepository)
    val profileService: ProfileService = ProfileService(producerRepository)
    val beatService: BeatService = BeatService(beatRepository)
    val statisticsService: StatisticsService = StatisticsService(statisticsRepository)
}
