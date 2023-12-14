package com.github.stephenwanjala.routes

import com.auth0.jwt.JWT
import com.github.stephenwanjala.auth.data.database.UserService
import com.github.stephenwanjala.auth.domain.model.DbToken
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
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

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
            salt = saltedHash.salt,
            phoneNumber = request.phoneNumber,
            fullName = request.fullName
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
    post("/signin") {
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

        println("Checking Token")
        val existingToken = repository.findTokenByUserId(user.id)
        existingToken?.let { token->
            call.respond(HttpStatusCode.OK, LoginResponse(token = token))
            return@post
        }

        println("Creating Token")
        val newToken = tokenService.generateToken(
            config = config,
            TokenClaim(
                name = "userId", value = user.id.toString()
            ),
            TokenClaim(name = "userName", value = user.userName),
            TokenClaim(name = "email", value = user.email),
        )
        println("New Token: $newToken")
        try {
           val tokenId= repository.saveToken(DbToken(userId = user.id, token = newToken))
            println("Token Id: $tokenId")
            call.respond(HttpStatusCode.OK, LoginResponse(token = newToken))
            return@post
        } catch (e:Exception){
            call.respond(HttpStatusCode.BadRequest,e.message ?: "Invalid token")
            return@post
        }
    }

}

fun Route.authenticate(){
    authenticate {
        get("/authenticate"){

            call.respond(HttpStatusCode.OK, "Authenticated")
        }
    }
}

fun Route.signout(repository: AuthRepository) {
    authenticate {
        post("/signout") {
            val token = call.request.authorization()?.removePrefix("Bearer ")
            if (token == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid token")
                return@post
            }
            try {

                repository.signOut(token.removePrefix("Bearer "))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid token")
                return@post
            }

    }
}
}



