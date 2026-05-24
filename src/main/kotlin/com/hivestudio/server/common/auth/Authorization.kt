package com.hivestudio.server.common.auth

import com.hivestudio.server.common.di.AppGraph
import com.hivestudio.server.common.model.UnauthorizedException
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall

data class AuthorizedProducer(
    val id: String,
    val token: String,
)

fun ApplicationCall.requireProducer(): AuthorizedProducer {
    val header = request.headers[HttpHeaders.Authorization]
        ?: throw UnauthorizedException("Нужна авторизация продюсера")

    val prefix = "Bearer "
    if (!header.startsWith(prefix, ignoreCase = true)) {
        throw UnauthorizedException("Некорректный заголовок Authorization")
    }

    val token = header.removePrefix(prefix).trim()
    if (token.isBlank()) {
        throw UnauthorizedException("Пустой bearer token")
    }

    val producer = runCatching { AppGraph.authService.getByToken(token) }
        .getOrElse { throw UnauthorizedException("Сессия продюсера не найдена или истекла") }
    return AuthorizedProducer(
        id = producer.id.toString(),
        token = token,
    )
}
