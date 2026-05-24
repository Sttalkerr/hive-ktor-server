package com.hivestudio.server.store

import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.Beat
import com.hivestudio.server.domain.model.BeatEvent
import com.hivestudio.server.domain.model.BeatStatistics
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class InMemoryHiveStore(
    seedBeats: List<Beat>,
    seedStatistics: Map<UUID, BeatStatistics>,
    seedEvents: List<BeatEvent>,
) {
    private val beatItems = CopyOnWriteArrayList(seedBeats)
    private val statisticsItems = ConcurrentHashMap(seedStatistics)
    private val eventItems = CopyOnWriteArrayList(seedEvents)

    fun getBeats(): List<Beat> = beatItems.toList()

    fun getBeat(beatId: UUID): Beat? = beatItems.firstOrNull { it.id == beatId }

    fun putBeat(beat: Beat) {
        beatItems.removeIf { it.id == beat.id }
        beatItems.add(beat)
    }

    fun removeBeat(beatId: UUID) {
        beatItems.removeIf { it.id == beatId }
        statisticsItems.remove(beatId)
        eventItems.removeIf { it.beatId == beatId }
    }

    fun getStatistics(beatId: UUID): BeatStatistics? = statisticsItems[beatId]

    fun putStatistics(statistics: BeatStatistics) {
        statisticsItems[statistics.beatId] = statistics
    }

    fun getEvents(beatId: UUID): List<BeatEvent> =
        eventItems.filter { it.beatId == beatId }.sortedBy { it.createdAt }

    fun putEvent(event: BeatEvent) {
        eventItems.add(event)
    }

    companion object {
        fun seeded(): InMemoryHiveStore =
            InMemoryHiveStore(
                seedBeats = DemoDataFactory.beats(),
                seedStatistics = DemoDataFactory.allStatistics(),
                seedEvents = DemoDataFactory.events(),
            )
    }
}
