package com.hivestudio.server.database.config

import io.ktor.server.application.Application
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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

fun Application.loadDatabaseSettings(): DatabaseSettings {
    val envConnectionString = firstEnv("HIVE_DATABASE_URL", "NEON_DATABASE_URL", "DATABASE_URL")
    val envUser = firstEnv("HIVE_DATABASE_USER", "PGUSER")
    val envPassword = firstEnv("HIVE_DATABASE_PASSWORD", "PGPASSWORD")
    val envConnect = firstEnv("HIVE_DATABASE_CONNECT_ON_STARTUP")?.toBooleanStrictOrNull()
    val envShowSql = firstEnv("HIVE_DATABASE_SHOW_SQL")?.toBooleanStrictOrNull()

    val configJdbcUrl = environment.config.property("database.jdbcUrl").getString()
    val configUser = environment.config.property("database.user").getString()
    val configPassword = environment.config.property("database.password").getString()
    val configConnect = environment.config.property("database.connectOnStartup").getString().toBoolean()
    val configShowSql = environment.config.property("database.showSql").getString().toBoolean()
    val parsedEnvConnection = envConnectionString?.let(::parseConnectionString)

    return DatabaseSettings(
        jdbcUrl = parsedEnvConnection?.jdbcUrl ?: normalizeJdbcUrl(configJdbcUrl),
        user = when {
            envUser != null -> envUser
            parsedEnvConnection?.user != null -> parsedEnvConnection.user
            else -> configUser
        },
        password = when {
            envPassword != null -> envPassword
            parsedEnvConnection?.password != null -> parsedEnvConnection.password
            else -> configPassword
        },
        connectOnStartup = envConnect ?: configConnect,
        showSql = envShowSql ?: configShowSql,
    )
}

private fun firstEnv(vararg names: String): String? =
    names.firstNotNullOfOrNull { name -> System.getenv(name)?.takeIf { it.isNotBlank() } }

private data class ParsedConnectionString(
    val jdbcUrl: String,
    val user: String?,
    val password: String?,
)

private fun parseConnectionString(value: String): ParsedConnectionString =
    when {
        value.startsWith("jdbc:postgresql://") -> ParsedConnectionString(
            jdbcUrl = value,
            user = null,
            password = null,
        )
        value.startsWith("postgresql://") || value.startsWith("postgres://") -> {
            val normalized = if (value.startsWith("postgres://")) {
                "postgresql://${value.removePrefix("postgres://")}"
            } else {
                value
            }
            val uri = URI(normalized)
            val userInfo = uri.userInfo?.split(":", limit = 2).orEmpty()
            val user = userInfo.getOrNull(0)?.decodeUrlComponent()
            val password = userInfo.getOrNull(1)?.decodeUrlComponent()
            val host = uri.host ?: error("Database host is missing in connection string")
            val port = if (uri.port != -1) ":${uri.port}" else ""
            val path = uri.rawPath ?: ""
            val query = uri.rawQuery?.let { "?$it" } ?: ""

            ParsedConnectionString(
                jdbcUrl = "jdbc:postgresql://$host$port$path$query",
                user = user,
                password = password,
            )
        }
        else -> ParsedConnectionString(
            jdbcUrl = value,
            user = null,
            password = null,
        )
    }

private fun normalizeJdbcUrl(value: String): String =
    when {
        value.startsWith("jdbc:postgresql://") -> value
        value.startsWith("postgresql://") -> "jdbc:$value"
        value.startsWith("postgres://") -> "jdbc:postgresql://${value.removePrefix("postgres://")}"
        else -> value
    }

private fun String.decodeUrlComponent(): String =
    URLDecoder.decode(this, StandardCharsets.UTF_8)
