package com.hivestudio.server.stats.repository

import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.BeatEventType
import com.hivestudio.server.store.InMemoryHiveStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryStatisticsRepositoryTest {
    @Test
    fun purchaseEventIncreasesRevenueAndCounter() {
        val store = InMemoryHiveStore.seeded()
        val repository = InMemoryStatisticsRepository(store)
        val beat = DemoDataFactory.beats().first()
        val before = repository.getStatistics(beat.id)

        repository.recordEvent(beat.id, BeatEventType.PURCHASE)

        val after = repository.getStatistics(beat.id)
        assertEquals(before.purchasesCount + 1, after.purchasesCount)
        assertTrue(after.revenueTotal > before.revenueTotal)
    }

    @Test
    fun historyReturnsRequestedNumberOfDays() {
        val store = InMemoryHiveStore.seeded()
        val repository = InMemoryStatisticsRepository(store)
        val beat = DemoDataFactory.beats().first()

        val history = repository.getHistory(beat.id, days = 7)

        assertEquals(7, history.size)
        assertTrue(history.any { it.playsCount > 0 })
    }
}
