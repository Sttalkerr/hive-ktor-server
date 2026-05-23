package com.hivestudio.server.store

import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.Beat
import com.hivestudio.server.domain.model.BeatStatistics
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class InMemoryHiveStore(
    seedBeats: List<Beat>,
    seedStatistics: Map<UUID, BeatStatistics>,
) {
    private val beatItems = CopyOnWriteArrayList(seedBeats)
    private val statisticsItems = ConcurrentHashMap(seedStatistics)

    fun getBeats(): List<Beat> = beatItems.toList()

    fun getBeat(beatId: UUID): Beat? = beatItems.firstOrNull { it.id == beatId }

    fun putBeat(beat: Beat) {
        beatItems.removeIf { it.id == beat.id }
        beatItems.add(beat)
    }

    fun removeBeat(beatId: UUID) {
        beatItems.removeIf { it.id == beatId }
        statisticsItems.remove(beatId)
    }

    fun getStatistics(beatId: UUID): BeatStatistics? = statisticsItems[beatId]

    fun putStatistics(statistics: BeatStatistics) {
        statisticsItems[statistics.beatId] = statistics
    }

    companion object {
        fun seeded(): InMemoryHiveStore =
            InMemoryHiveStore(
                seedBeats = DemoDataFactory.beats(),
                seedStatistics = DemoDataFactory.allStatistics(),
            )
    }
}
