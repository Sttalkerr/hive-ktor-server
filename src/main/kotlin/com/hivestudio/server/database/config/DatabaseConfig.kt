package com.hivestudio.server.database.config

import io.ktor.server.application.Application

data class DatabaseSettings(
    val jdbcUrl: String,
    val user: String,
    val password: String,
    val connectOnStartup: Boolean,
    val showSql: Boolean,
)

fun Application.configureDatabase() {
    val settings = loadDatabaseSettings()
    DatabaseFactory.initialize(environment, settings)
}

fun Application.loadDatabaseSettings(): DatabaseSettings =
    DatabaseSettings(
        jdbcUrl = environment.config.property("database.jdbcUrl").getString(),
        user = environment.config.property("database.user").getString(),
        password = environment.config.property("database.password").getString(),
        connectOnStartup = environment.config.property("database.connectOnStartup").getString().toBoolean(),
        showSql = environment.config.property("database.showSql").getString().toBoolean(),
    )
