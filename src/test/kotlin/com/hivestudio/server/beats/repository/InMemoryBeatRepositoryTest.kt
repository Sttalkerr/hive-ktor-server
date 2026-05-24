package com.hivestudio.server.beats.repository

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.store.InMemoryHiveStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryBeatRepositoryTest {
    @Test
    fun createAddsNewBeatToStore() {
        val store = InMemoryHiveStore.seeded()
        val repository = InMemoryBeatRepository(store)
        val producerId = DemoDataFactory.producer().id

        val created = repository.create(
            producerId,
            CreateBeatRequest(
                title = "North District",
                genre = "Drill",
                bpm = 144,
                price = 3190.0,
                description = "Aggressive drill beat",
                mp3FileName = "north-district.mp3",
                coverImageFileName = "north-district-cover.jpg",
            )
        )

        assertEquals(3, repository.getAll(producerId).size)
        assertTrue(repository.getById(producerId, created.id).title == "North District")
        assertEquals(0, store.getStatistics(created.id)?.playsCount)
    }

    @Test
    fun deleteRemovesBeatFromStore() {
        val store = InMemoryHiveStore.seeded()
        val repository = InMemoryBeatRepository(store)
        val producerId = DemoDataFactory.producer().id
        val beatId = DemoDataFactory.beats().first().id

        repository.delete(producerId, beatId)

        assertEquals(1, repository.getAll(producerId).size)
        assertEquals(null, store.getStatistics(beatId))
    }
}
