package com.github.stephenwanjala.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.github.stephenwanjala.auth.data.database.UserService
import com.github.stephenwanjala.auth.domain.repository.AuthRepository
import com.github.stephenwanjala.auth.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.jetbrains.exposed.sql.select

data class UserPrincipal(val payload: Payload) : Principal
fun Application.configureSecurity(config: TokenConfig,authRepository: AuthRepository) {

    // Please read the jwt property from the config file if you are using EngineMain
    val jwtRealm = "Rev_vault"
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secrete))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { credential ->
                val token = request.headers["Authorization"]?.removePrefix("Bearer ") ?: return@validate null

                if (authRepository.isTokenBlacklisted(token) || authRepository.isTokenValid(token).not()) {
                    null
                } else {
                    // Your existing validation logic here
                    if (credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
                }
            }
        }
    }
}
