package com.github.stephenwanjala

import com.github.stephenwanjala.auth.data.repository.AuthRepositoryImpl
import com.github.stephenwanjala.auth.security.hashing.SHA256HashingService
import com.github.stephenwanjala.auth.security.token.JwtTokenService
import com.github.stephenwanjala.auth.security.token.TokenConfig
import com.github.stephenwanjala.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val schemas = configureDatabases()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 60 * 60 * 1000L * 24L,
        secrete = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    val tokenService = JwtTokenService()
    val authRepository = AuthRepositoryImpl(schemas.userService)
    configureSecurity(config = tokenConfig,
        authRepository = authRepository
        )

    configureSerialization()
    configureMonitoring()
    configureStatusPages()
    configureHTTP()
    configureRouting(
        hashingService = hashingService,
        tokenConfig = tokenConfig,
        tokenService = tokenService,
        authRepository = authRepository
    )
}
