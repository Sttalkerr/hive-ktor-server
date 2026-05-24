package com.hivestudio.server.profile.repository

import com.hivestudio.server.auth.model.LoginRequest
import com.hivestudio.server.auth.model.RegisterRequest
import com.hivestudio.server.demo.DemoDataFactory
import com.hivestudio.server.domain.model.Producer
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryProducerRepository : ProducerRepository {
    private val producers = ConcurrentHashMap<String, ProducerRecord>()
    private val tokens = ConcurrentHashMap<String, String>()

    init {
        val seed = DemoDataFactory.producer()
        producers[seed.email.lowercase()] = ProducerRecord(
            producer = seed,
            plainPassword = "secret123",
        )
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
            passwordHash = "hash:${request.password}",
            stageName = request.stageName.trim(),
            createdAt = now,
            updatedAt = now,
        )
        producers[emailKey] = ProducerRecord(
            producer = producer,
            plainPassword = request.password,
        )
        return producer
    }

    override fun login(request: LoginRequest): Producer {
        val emailKey = request.email.trim().lowercase()
        val record = producers[emailKey]
            ?: throw IllegalArgumentException("Producer with email ${request.email} not found")
        if (record.plainPassword != request.password) {
            throw IllegalArgumentException("Invalid password")
        }
        return record.producer
    }

    override fun issueToken(producer: Producer): String {
        val token = "hive-${UUID.randomUUID()}"
        tokens[token] = producer.email.lowercase()
        return token
    }

    override fun getByToken(token: String): Producer {
        val emailKey = tokens[token]
            ?: throw NoSuchElementException("Session token not found")
        return producers[emailKey]?.producer
            ?: throw NoSuchElementException("Producer for session token not found")
    }
}

private data class ProducerRecord(
    val producer: Producer,
    val plainPassword: String,
)
