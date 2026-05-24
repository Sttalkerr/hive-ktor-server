package com.hivestudio.server.common.di

import com.hivestudio.server.auth.service.AuthService
import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.beats.repository.InMemoryBeatRepository
import com.hivestudio.server.beats.service.BeatService
import com.hivestudio.server.profile.repository.InMemoryProducerRepository
import com.hivestudio.server.profile.repository.ProducerRepository
import com.hivestudio.server.profile.service.ProfileService
import com.hivestudio.server.stats.repository.InMemoryStatisticsRepository
import com.hivestudio.server.stats.repository.StatisticsRepository
import com.hivestudio.server.stats.service.StatisticsService
import com.hivestudio.server.storage.FileStorageService
import com.hivestudio.server.storage.StorageSettings
import com.hivestudio.server.store.InMemoryHiveStore

object AppGraph {
    private val store = InMemoryHiveStore.seeded()
    val storageSettings: StorageSettings = StorageSettings(uploadDir = "./storage/uploads")
    val fileStorageService: FileStorageService = FileStorageService(storageSettings)

    val producerRepository: ProducerRepository = InMemoryProducerRepository()
    val beatRepository: BeatRepository = InMemoryBeatRepository(store)
    val statisticsRepository: StatisticsRepository = InMemoryStatisticsRepository(store)

    val authService: AuthService = AuthService(producerRepository)
    val profileService: ProfileService = ProfileService(producerRepository, beatRepository, statisticsRepository)
    val beatService: BeatService = BeatService(beatRepository, producerRepository)
    val statisticsService: StatisticsService = StatisticsService(statisticsRepository, beatRepository)
}
