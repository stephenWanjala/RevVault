package com.github.stephenwanjala

import com.github.stephenwanjala.auth.data.repository.AuthRepositoryImpl
import com.github.stephenwanjala.auth.security.hashing.SHA256HashingService
import com.github.stephenwanjala.auth.security.token.JwtTokenService
import com.github.stephenwanjala.auth.security.token.TokenConfig
import com.github.stephenwanjala.plugins.*
import io.ktor.server.application.*
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.ConcurrentHashMap

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

val rateLimiters = ConcurrentHashMap<String, RateLimit>()
val mutex = Mutex()

fun Application.module() {

    val POSTGRES_HOST = System.getenv("POSTGRES_HOST")
    val POSTGRES_PORT = System.getenv("POSTGRES_PORT") ?: "5432"
    val POSTGRES_DB = System.getenv("POSTGRES_DB")
    val POSTGRES_USER = System.getenv("POSTGRES_USER")
    val POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD")
    val POSTGRES_URL = "jdbc:postgresql://$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB"
    val schemas = configureDatabases(db_url = POSTGRES_URL, db_user = POSTGRES_USER, db_password = POSTGRES_PASSWORD)
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 60 * 60 * 1000L * 24L,
        secrete = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    val tokenService = JwtTokenService()
    val authRepository = AuthRepositoryImpl(schemas.userService)
    configureSecurity(
        config = tokenConfig,
        authRepository = authRepository
    )

    configureSerialization()
    configureMonitoring()
    configureStatusPages()
    configureHTTP()
    rateLimitingModule()
    configureRouting(
        hashingService = hashingService,
        tokenConfig = tokenConfig,
        tokenService = tokenService,
        authRepository = authRepository
    )
}
