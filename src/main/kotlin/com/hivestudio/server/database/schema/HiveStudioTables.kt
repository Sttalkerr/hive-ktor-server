package com.hivestudio.server.database.schema

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object ProducersTable : UUIDTable("producers") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val stageName = varchar("stage_name", 120)
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
}

object BeatsTable : UUIDTable("beats") {
    val producerId = reference("producer_id", ProducersTable)
    val title = varchar("title", 160)
    val genre = varchar("genre", 80)
    val bpm = integer("bpm")
    val price = decimal("price", precision = 10, scale = 2)
    val description = text("description")
    val mp3FileName = varchar("mp3_file_name", 255)
    val mp3StoragePath = text("mp3_storage_path")
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
}

object BeatStatisticsTable : Table("beat_statistics") {
    val beatId = reference("beat_id", BeatsTable)
    val playsCount = integer("plays_count").default(0)
    val likesCount = integer("likes_count").default(0)
    val purchasesCount = integer("purchases_count").default(0)
    val revenueTotal = decimal("revenue_total", precision = 12, scale = 2).default(java.math.BigDecimal.ZERO)
    val updatedAt = timestampWithTimeZone("updated_at")

    override val primaryKey = PrimaryKey(beatId)
}

object BeatEventsTable : UUIDTable("beat_events") {
    val beatId = reference("beat_id", BeatsTable)
    val eventType = varchar("event_type", 20)
    val eventValue = decimal("event_value", precision = 12, scale = 2).default(java.math.BigDecimal.ZERO)
    val createdAt = timestampWithTimeZone("created_at")
}

object HiveStudioTables {
    val allTables: List<Table> = listOf(
        ProducersTable,
        BeatsTable,
        BeatStatisticsTable,
        BeatEventsTable,
    )
}
