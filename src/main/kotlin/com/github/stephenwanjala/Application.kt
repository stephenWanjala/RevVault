package com.github.stephenwanjala

import com.github.stephenwanjala.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureDatabases()
    configureRouting()
}
