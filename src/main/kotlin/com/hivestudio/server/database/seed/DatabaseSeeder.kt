package com.hivestudio.server.database.seed

import com.hivestudio.server.database.schema.BeatEventsTable
import com.hivestudio.server.database.schema.BeatStatisticsTable
import com.hivestudio.server.database.schema.BeatsTable
import com.hivestudio.server.database.schema.ProducersTable
import com.hivestudio.server.demo.DemoDataFactory
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

object DatabaseSeeder {
    fun seedIfNeeded() {
        if (ProducersTable.selectAll().any()) return

        val producer = DemoDataFactory.producer()
        ProducersTable.insert {
            it[id] = EntityID(producer.id, ProducersTable)
            it[email] = producer.email
            it[passwordHash] = producer.passwordHash
            it[stageName] = producer.stageName
            it[bio] = producer.bio
            it[city] = producer.city
            it[contactTag] = producer.contactTag
            it[avatarFileName] = producer.avatarFileName
            it[avatarStoragePath] = producer.avatarStoragePath
            it[createdAt] = producer.createdAt.toOffsetUtc()
            it[updatedAt] = producer.updatedAt.toOffsetUtc()
        }

        DemoDataFactory.beats().forEach { beat ->
            BeatsTable.insert {
                it[id] = EntityID(beat.id, BeatsTable)
                it[producerId] = EntityID(beat.producerId, ProducersTable)
                it[title] = beat.title
                it[genre] = beat.genre
                it[bpm] = beat.bpm
                it[price] = beat.price
                it[description] = beat.description
                it[mp3FileName] = beat.mp3FileName
                it[coverImageFileName] = beat.coverImageFileName
                it[mp3StoragePath] = beat.mp3StoragePath
                it[coverImageStoragePath] = beat.coverImageStoragePath
                it[createdAt] = beat.createdAt.toOffsetUtc()
                it[updatedAt] = beat.updatedAt.toOffsetUtc()
            }
        }

        DemoDataFactory.allStatistics().values.forEach { statistics ->
            BeatStatisticsTable.insert {
                it[beatId] = EntityID(statistics.beatId, BeatsTable)
                it[playsCount] = statistics.playsCount
                it[likesCount] = statistics.likesCount
                it[purchasesCount] = statistics.purchasesCount
                it[revenueTotal] = statistics.revenueTotal
                it[updatedAt] = statistics.updatedAt.toOffsetUtc()
            }
        }

        DemoDataFactory.events().forEach { event ->
            BeatEventsTable.insert {
                it[id] = EntityID(event.id, BeatEventsTable)
                it[beatId] = EntityID(event.beatId, BeatsTable)
                it[eventType] = event.eventType.name
                it[eventValue] = event.eventValue
                it[createdAt] = event.createdAt.toOffsetUtc()
            }
        }
    }
}

private fun Instant.toOffsetUtc(): OffsetDateTime = OffsetDateTime.ofInstant(this, ZoneOffset.UTC)
