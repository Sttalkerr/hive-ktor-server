package com.hivestudio.server.database.config

import com.hivestudio.server.database.schema.HiveStudioTables
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    private var dataSource: HikariDataSource? = null

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
            username = settings.user
            password = settings.password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource!!)

        if (settings.showSql) {
            environment.log.info("PostgreSQL connection initialized with SQL logging requested by config.")
        }

        environment.log.info(
            "Database connection initialized. Registered schema tables: ${HiveStudioTables.allTables.joinToString { it.tableName }}"
        )
    }
}
