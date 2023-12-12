package com.github.stephenwanjala

import com.github.stephenwanjala.auth.security.token.TokenConfig
import com.github.stephenwanjala.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 60 * 60 * 1000L * 24L,
        secrete = System.getenv("JWT_SECRET")
    )
    configureSecurity(config = tokenConfig)
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureDatabases()
    configureRouting()
}
