package com.hivestudio.server.common.config

import com.hivestudio.server.common.model.ErrorResponse
import com.hivestudio.server.common.model.UnauthorizedException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<NoSuchElementException> { call, cause ->
            call.respond(
                status = HttpStatusCode.NotFound,
                message = ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    message = cause.message ?: "Resource not found",
                )
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    message = cause.message ?: "Invalid request data",
                )
            )
        }

        exception<UnauthorizedException> { call, cause ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ErrorResponse(
                    status = HttpStatusCode.Unauthorized.value,
                    message = cause.message ?: "Authorization required",
                )
            )
        }
    }
}
