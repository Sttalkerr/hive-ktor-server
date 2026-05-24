package com.hivestudio.server.common.config

import com.hivestudio.server.auth.routing.authRoutes
import com.hivestudio.server.beats.routing.beatRoutes
import com.hivestudio.server.common.di.AppGraph
import com.hivestudio.server.common.model.HealthResponse
import com.hivestudio.server.profile.routing.profileRoutes
import com.hivestudio.server.stats.routing.statisticsRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.staticFiles
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.io.File

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(
                HealthResponse(
                    name = "Hive Studio Server",
                    version = "1.0.0",
                    status = "ok",
                )
            )
        }

        staticFiles(
            remotePath = "/uploads",
            dir = File(AppGraph.storageSettings.uploadDir),
        )

        route("/api/v1") {
            authRoutes()
            profileRoutes()
            beatRoutes()
            statisticsRoutes()
        }
    }
}
