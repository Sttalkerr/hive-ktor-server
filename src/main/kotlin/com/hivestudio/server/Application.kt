package com.hivestudio.server

import com.hivestudio.server.common.config.configureHttp
import com.hivestudio.server.common.config.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureHttp()
    configureRouting()
}
