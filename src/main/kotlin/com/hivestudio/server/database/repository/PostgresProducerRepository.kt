package com.hivestudio.server.database.repository

import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.database.config.DatabaseFactory
import com.hivestudio.server.database.schema.ProducersTable
import com.hivestudio.server.domain.model.Producer
import com.hivestudio.server.profile.model.UpdateProfileRequest
import com.hivestudio.server.profile.repository.ProducerRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PostgresProducerRepository : ProducerRepository {
    private val tokens = ConcurrentHashMap<String, UUID>()

    override fun register(request: RegisterRequest): Producer = DatabaseFactory.query {
        val emailKey = request.email.trim().lowercase()
        if (ProducersTable.selectAll().any { it[ProducersTable.email].lowercase() == emailKey }) {
            throw IllegalArgumentException("Producer with email ${request.email} already exists")
        }

        val now = Instant.now()
        val producerId = UUID.randomUUID()
        ProducersTable.insert {
            it[id] = EntityID(producerId, ProducersTable)
            it[email] = request.email.trim()
            it[passwordHash] = "hash:${request.password}"
            it[stageName] = request.stageName.trim()
            it[bio] = ""
            it[city] = ""
            it[contactTag] = ""
            it[avatarFileName] = null
            it[avatarStoragePath] = null
            it[createdAt] = now.toOffsetUtc()
            it[updatedAt] = now.toOffsetUtc()
        }
        findProducerById(producerId)
    }

    override fun login(request: LoginRequest): Producer = DatabaseFactory.query {
        val producer = ProducersTable
            .selectAll()
            .firstOrNull { it[ProducersTable.email].equals(request.email.trim(), ignoreCase = true) }
            ?.toProducer()
            ?: throw IllegalArgumentException("Producer with email ${request.email} not found")

        if (producer.passwordHash != "hash:${request.password}") {
            throw IllegalArgumentException("Invalid password")
        }
        producer
    }

    override fun issueToken(producer: Producer): String {
        val token = "hive-${UUID.randomUUID()}"
        tokens[token] = producer.id
        return token
    }

    override fun getByToken(token: String): Producer {
        val producerId = tokens[token] ?: throw NoSuchElementException("Session token not found")
        return getById(producerId)
    }

    override fun getById(producerId: UUID): Producer = DatabaseFactory.query {
        findProducerById(producerId)
    }

    override fun updateProfile(producerId: UUID, request: UpdateProfileRequest): Producer = DatabaseFactory.query {
        val updatedRows = ProducersTable.update({ ProducersTable.id eq producerId }) {
            it[stageName] = request.stageName.trim()
            it[bio] = request.bio.trim()
            it[city] = request.city.trim()
            it[contactTag] = request.contactTag.trim()
            it[updatedAt] = Instant.now().toOffsetUtc()
        }
        if (updatedRows == 0) {
            throw NoSuchElementException("Producer $producerId not found")
        }
        findProducerById(producerId)
    }

    override fun updateAvatar(producerId: UUID, avatarFileName: String): Producer = DatabaseFactory.query {
        val updatedRows = ProducersTable.update({ ProducersTable.id eq producerId }) {
            it[ProducersTable.avatarFileName] = avatarFileName
            it[avatarStoragePath] = "/uploads/$avatarFileName"
            it[updatedAt] = Instant.now().toOffsetUtc()
        }
        if (updatedRows == 0) {
            throw NoSuchElementException("Producer $producerId not found")
        }
        findProducerById(producerId)
    }

    private fun findProducerById(producerId: UUID): Producer =
        ProducersTable
            .selectAll()
            .firstOrNull { it[ProducersTable.id].value == producerId }
            ?.toProducer()
            ?: throw NoSuchElementException("Producer $producerId not found")
}

private fun ResultRow.toProducer(): Producer =
    Producer(
        id = this[ProducersTable.id].value,
        email = this[ProducersTable.email],
        passwordHash = this[ProducersTable.passwordHash],
        stageName = this[ProducersTable.stageName],
        bio = this[ProducersTable.bio],
        city = this[ProducersTable.city],
        contactTag = this[ProducersTable.contactTag],
        avatarFileName = this[ProducersTable.avatarFileName],
        avatarStoragePath = this[ProducersTable.avatarStoragePath],
        createdAt = this[ProducersTable.createdAt].toInstant(),
        updatedAt = this[ProducersTable.updatedAt].toInstant(),
    )

private fun Instant.toOffsetUtc(): OffsetDateTime = OffsetDateTime.ofInstant(this, ZoneOffset.UTC)
