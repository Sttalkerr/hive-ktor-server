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

        val created = repository.create(
            CreateBeatRequest(
                title = "North District",
                genre = "Drill",
                bpm = 144,
                price = 3190.0,
                description = "Aggressive drill beat",
                mp3FileName = "north-district.mp3",
            )
        )

        assertEquals(3, repository.getAll().size)
        assertTrue(repository.getById(created.id).title == "North District")
        assertEquals(0, store.getStatistics(created.id)?.playsCount)
    }

    @Test
    fun deleteRemovesBeatFromStore() {
        val store = InMemoryHiveStore.seeded()
        val repository = InMemoryBeatRepository(store)
        val beatId = DemoDataFactory.beats().first().id

        repository.delete(beatId)

        assertEquals(1, repository.getAll().size)
        assertEquals(null, store.getStatistics(beatId))
    }
}
