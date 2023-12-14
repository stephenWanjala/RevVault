package com.github.stephenwanjala.plugins

import com.github.stephenwanjala.auth.domain.repository.AuthRepository
import com.github.stephenwanjala.auth.security.hashing.HashingService
import com.github.stephenwanjala.auth.security.token.TokenConfig
import com.github.stephenwanjala.auth.security.token.TokenService
import com.github.stephenwanjala.routes.authenticate
import com.github.stephenwanjala.routes.signIn
import com.github.stephenwanjala.routes.signUp
import com.github.stephenwanjala.routes.signout
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    hashingService: HashingService,
    authRepository: AuthRepository,
    tokenConfig: TokenConfig,
    tokenService: TokenService
) {
    routing {
        authenticate()
        signIn(
            hashingService=hashingService,
            repository=authRepository,
            config=tokenConfig,
            tokenService = tokenService
        )
        signUp(
            hashingService=hashingService,
            authRepository=authRepository
        )

        signout(repository = authRepository)
    }
}
