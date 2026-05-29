package com.hivestudio.server.database.config

import com.hivestudio.server.database.schema.HiveStudioTables
import com.hivestudio.server.database.seed.DatabaseSeeder
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private var dataSource: HikariDataSource? = null
    private var database: Database? = null

    fun initialize(
        environment: ApplicationEnvironment,
        settings: DatabaseSettings,
    ) {
        if (!settings.connectOnStartup) {
            environment.log.info(
                "Database auto-connect is disabled. PostgreSQL schema is prepared, but startup will not open a connection yet."
            )
            return
        }

        if (dataSource != null) {
            environment.log.info("Database connection is already initialized.")
            return
        }

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = settings.jdbcUrl
            if (settings.user.isNotBlank()) {
                username = settings.user
            }
            if (settings.password.isNotBlank()) {
                password = settings.password
            }
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        dataSource = HikariDataSource(hikariConfig)
        database = Database.connect(dataSource!!)

        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(*HiveStudioTables.allTables.toTypedArray())
            DatabaseSeeder.seedIfNeeded()
        }

        if (settings.showSql) {
            environment.log.info("PostgreSQL connection initialized with SQL logging requested by config.")
        }

        environment.log.info(
            "Database connection initialized. Registered schema tables: ${HiveStudioTables.allTables.joinToString { it.tableName }}"
        )
    }

    fun isConnected(): Boolean = database != null

    fun <T> query(block: () -> T): T {
        val currentDatabase = database ?: error("Database is not initialized")
        return transaction(currentDatabase) { block() }
    }
}
