package com.github.stephenwanjala.routes

import com.github.stephenwanjala.auth.domain.model.User
import com.github.stephenwanjala.auth.domain.repository.AuthRepository
import com.github.stephenwanjala.auth.domain.responses.LoginRequest
import com.github.stephenwanjala.auth.domain.responses.LoginResponse
import com.github.stephenwanjala.auth.domain.responses.SignUpRequest
import com.github.stephenwanjala.auth.security.hashing.HashingService
import com.github.stephenwanjala.auth.security.hashing.SaltedHash
import com.github.stephenwanjala.auth.security.token.TokenClaim
import com.github.stephenwanjala.auth.security.token.TokenConfig
import com.github.stephenwanjala.auth.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(hashingService: HashingService, authRepository: AuthRepository) {

    post("/signup") {
        val request = call.receiveNullable<SignUpRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        // check if fields are empty
        if (request.authInfo.email.isEmpty() || request.authInfo.password.isEmpty() || request.authInfo.userName.isEmpty()

        ) {
            call.respond(HttpStatusCode.Conflict, "Fields cannot be empty")
            return@post
        }

        // check if user exists
        // check if user exists
        val userExist = authRepository.findUserByEmail(request.authInfo.email)
        if (userExist != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return@post
        }
        val saltedHash = hashingService.generateSaltedHash(value = request.authInfo.password)
        val user = User(
            userName = request.authInfo.userName,
            email = request.authInfo.email,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val userId = authRepository.signUp(user)
        if (userId > 0) {
            call.respond(HttpStatusCode.Created, "User created successfully")

        } else {
            call.respond(HttpStatusCode.Conflict, "User creation failed")
            return@post
        }
    }
}

fun Route.signIn(
    hashingService: HashingService, repository: AuthRepository, config: TokenConfig, tokenService: TokenService
) {
    post("signin") {
        val request = call.receiveNullable<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        if (request.email.isEmpty() || request.password.isEmpty()) {
            call.respond(HttpStatusCode.Conflict, "Fields cannot be empty")
            return@post
        }
        val user = repository.findUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "User does not exist")
            return@post
        }

        val saltedHash = SaltedHash(salt = user.salt, hash = user.password)
        val isValidPassword = hashingService.verify(
            value = request.password, saltedHash = saltedHash
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Invalid password")
            return@post
        }

        val token = tokenService.generateToken(
            config = config,
            TokenClaim(
                name = "userId", value = user.id.toString()
            ),
            TokenClaim(name = "userName", value = user.userName),
            TokenClaim(name = "email", value = user.email),
        )
        call.respond(HttpStatusCode.OK, LoginResponse(token = token))
    }
}

fun Route.authenticate(){
    authenticate {
        get("/authenticate"){

            call.respond(HttpStatusCode.OK, "Authenticated")
        }
    }
}
