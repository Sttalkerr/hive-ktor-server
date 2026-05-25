package com.hivestudio.server.database.repository

import com.hivestudio.server.beats.model.CreateBeatRequest
import com.hivestudio.server.beats.model.UpdateBeatRequest
import com.hivestudio.server.beats.repository.BeatRepository
import com.hivestudio.server.database.config.DatabaseFactory
import com.hivestudio.server.database.schema.BeatStatisticsTable
import com.hivestudio.server.database.schema.BeatsTable
import com.hivestudio.server.domain.model.Beat
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class PostgresBeatRepository : BeatRepository {
    override fun getAll(producerId: UUID, query: String?): List<Beat> = DatabaseFactory.query {
        BeatsTable
            .selectAll()
            .map(::toBeat)
            .filter { it.producerId == producerId }
            .filterByQuery(query)
            .sortedByDescending { it.createdAt }
    }

    override fun getAllPublic(query: String?): List<Beat> = DatabaseFactory.query {
        BeatsTable
            .selectAll()
            .map(::toBeat)
            .filterByQuery(query)
            .sortedByDescending { it.createdAt }
    }

    override fun getPublicById(beatId: UUID): Beat = DatabaseFactory.query {
        BeatsTable
            .selectAll()
            .firstOrNull { it[BeatsTable.id].value == beatId }
            ?.let(::toBeat)
            ?: throw NoSuchElementException("Beat $beatId not found")
    }

    override fun getById(producerId: UUID, beatId: UUID): Beat = DatabaseFactory.query {
        val beat = BeatsTable
            .selectAll()
            .firstOrNull { it[BeatsTable.id].value == beatId }
            ?.let(::toBeat)
            ?: throw NoSuchElementException("Beat $beatId not found")
        if (beat.producerId != producerId) {
            throw NoSuchElementException("Beat $beatId not found")
        }
        beat
    }

    override fun create(producerId: UUID, request: CreateBeatRequest): Beat = DatabaseFactory.query {
        val now = Instant.now()
        val beatId = UUID.randomUUID()
        BeatsTable.insert {
            it[id] = EntityID(beatId, BeatsTable)
            it[BeatsTable.producerId] = EntityID(producerId, com.hivestudio.server.database.schema.ProducersTable)
            it[title] = request.title.trim()
            it[genre] = request.genre.trim()
            it[bpm] = request.bpm
            it[price] = BigDecimal.valueOf(request.price)
            it[description] = request.description.trim()
            it[mp3FileName] = request.mp3FileName
            it[coverImageFileName] = request.coverImageFileName
            it[mp3StoragePath] = "/uploads/${request.mp3FileName}"
            it[coverImageStoragePath] = "/uploads/${request.coverImageFileName}"
            it[createdAt] = now.toOffsetUtc()
            it[updatedAt] = now.toOffsetUtc()
        }
        BeatStatisticsTable.insert {
            it[BeatStatisticsTable.beatId] = EntityID(beatId, BeatsTable)
            it[playsCount] = 0
            it[likesCount] = 0
            it[purchasesCount] = 0
            it[revenueTotal] = BigDecimal.ZERO
            it[updatedAt] = now.toOffsetUtc()
        }
        findOwnedBeat(producerId, beatId)
    }

    override fun update(producerId: UUID, beatId: UUID, request: UpdateBeatRequest): Beat = DatabaseFactory.query {
        findOwnedBeat(producerId, beatId)
        BeatsTable.update({ (BeatsTable.id eq beatId) and (BeatsTable.producerId eq producerId) }) {
            it[title] = request.title.trim()
            it[genre] = request.genre.trim()
            it[bpm] = request.bpm
            it[price] = BigDecimal.valueOf(request.price)
            it[description] = request.description.trim()
            it[updatedAt] = Instant.now().toOffsetUtc()
        }
        findOwnedBeat(producerId, beatId)
    }

    override fun delete(producerId: UUID, beatId: UUID) {
        DatabaseFactory.query {
            findOwnedBeat(producerId, beatId)
            BeatStatisticsTable.deleteWhere { BeatStatisticsTable.beatId eq beatId }
            com.hivestudio.server.database.schema.BeatEventsTable.deleteWhere { com.hivestudio.server.database.schema.BeatEventsTable.beatId eq beatId }
            BeatsTable.deleteWhere { (BeatsTable.id eq beatId) and (BeatsTable.producerId eq producerId) }
        }
    }

    private fun findOwnedBeat(producerId: UUID, beatId: UUID): Beat {
        val beat = BeatsTable
            .selectAll()
            .firstOrNull { it[BeatsTable.id].value == beatId }
            ?.let(::toBeat)
            ?: throw NoSuchElementException("Beat $beatId not found")
        if (beat.producerId != producerId) {
            throw NoSuchElementException("Beat $beatId not found")
        }
        return beat
    }
}

private fun List<Beat>.filterByQuery(query: String?): List<Beat> {
    if (query.isNullOrBlank()) return this
    return filter {
        it.title.contains(query, ignoreCase = true) || it.genre.contains(query, ignoreCase = true)
    }
}

private fun toBeat(row: ResultRow): Beat =
    Beat(
        id = row[BeatsTable.id].value,
        producerId = row[BeatsTable.producerId].value,
        title = row[BeatsTable.title],
        genre = row[BeatsTable.genre],
        bpm = row[BeatsTable.bpm],
        price = row[BeatsTable.price],
        description = row[BeatsTable.description],
        mp3FileName = row[BeatsTable.mp3FileName],
        coverImageFileName = row[BeatsTable.coverImageFileName],
        mp3StoragePath = row[BeatsTable.mp3StoragePath],
        coverImageStoragePath = row[BeatsTable.coverImageStoragePath],
        createdAt = row[BeatsTable.createdAt].toInstant(),
        updatedAt = row[BeatsTable.updatedAt].toInstant(),
    )

private fun Instant.toOffsetUtc(): OffsetDateTime = OffsetDateTime.ofInstant(this, ZoneOffset.UTC)
