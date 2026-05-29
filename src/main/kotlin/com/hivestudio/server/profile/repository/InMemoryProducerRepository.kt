package com.hivestudio.server.profile.repository

import com.hivestudio.server.common.security.PasswordHasher
import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.Producer
import com.hivestudio.server.profile.model.UpdateProfileRequest
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryProducerRepository : ProducerRepository {
    private val producers = ConcurrentHashMap<String, Producer>()
    private val producersById = ConcurrentHashMap<UUID, String>()
    private val tokens = ConcurrentHashMap<String, String>()

    init {
        val seed = DemoDataFactory.producer()
        val emailKey = seed.email.lowercase()
        producers[emailKey] = seed
        producersById[seed.id] = emailKey
    }

    override fun register(request: RegisterRequest): Producer {
        val emailKey = request.email.trim().lowercase()
        if (producers.containsKey(emailKey)) {
            throw IllegalArgumentException("Producer with email ${request.email} already exists")
        }

        val now = Instant.now()
        val producer = Producer(
            id = UUID.randomUUID(),
            email = request.email.trim(),
            passwordHash = PasswordHasher.hash(request.password),
            stageName = request.stageName.trim(),
            bio = "",
            city = "",
            contactTag = "",
            avatarFileName = null,
            avatarStoragePath = null,
            createdAt = now,
            updatedAt = now,
        )
        producers[emailKey] = producer
        producersById[producer.id] = emailKey
        return producer
    }

    override fun login(request: LoginRequest): Producer {
        val emailKey = request.email.trim().lowercase()
        val producer = producers[emailKey]
            ?: throw IllegalArgumentException("Producer with email ${request.email} not found")
        if (!PasswordHasher.verify(request.password, producer.passwordHash)) {
            throw IllegalArgumentException("Invalid password")
        }
        if (PasswordHasher.needsUpgrade(producer.passwordHash)) {
            val upgraded = producer.copy(passwordHash = PasswordHasher.hash(request.password))
            save(upgraded)
            return upgraded
        }
        return producer
    }

    override fun issueToken(producer: Producer): String {
        val token = "hive-${UUID.randomUUID()}"
        tokens[token] = producer.email.lowercase()
        return token
    }

    override fun getByToken(token: String): Producer {
        val emailKey = tokens[token]
            ?: throw NoSuchElementException("Session token not found")
        return producers[emailKey]
            ?: throw NoSuchElementException("Producer for session token not found")
    }

    override fun getById(producerId: UUID): Producer {
        val emailKey = producersById[producerId]
            ?: throw NoSuchElementException("Producer $producerId not found")
        return producers[emailKey]
            ?: throw NoSuchElementException("Producer $producerId not found")
    }

    override fun updateProfile(producerId: UUID, request: UpdateProfileRequest): Producer {
        val current = getById(producerId)
        val updated = current.copy(
            stageName = request.stageName.trim(),
            bio = request.bio.trim(),
            city = request.city.trim(),
            contactTag = request.contactTag.trim(),
            updatedAt = Instant.now(),
        )
        save(updated)
        return updated
    }

    override fun updateAvatar(producerId: UUID, avatarFileName: String): Producer {
        val current = getById(producerId)
        val updated = current.copy(
            avatarFileName = avatarFileName,
            avatarStoragePath = "/uploads/$avatarFileName",
            updatedAt = Instant.now(),
        )
        save(updated)
        return updated
    }

    private fun save(producer: Producer) {
        val emailKey = producer.email.lowercase()
        check(producers.containsKey(emailKey)) { "Producer ${producer.email} not found" }
        producers[emailKey] = producer
        producersById[producer.id] = emailKey
    }
}
