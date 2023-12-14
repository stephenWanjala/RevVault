package com.github.stephenwanjala.auth.data.repository

import com.auth0.jwt.JWT
import com.github.stephenwanjala.auth.data.database.UserService
import com.github.stephenwanjala.auth.domain.model.DbToken
import com.github.stephenwanjala.auth.domain.model.User
import com.github.stephenwanjala.auth.domain.repository.AuthRepository
import com.github.stephenwanjala.auth.security.token.TokenConfig
import com.github.stephenwanjala.auth.security.token.TokenService
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class AuthRepositoryImpl(
    private val userService: UserService,
) : AuthRepository {
    override suspend fun signUp(user: User): Int = userService.create(user)

    override suspend fun signOut(token: String) {
        if (isTokenValid(token)) {
            addToBlacklist(token)

        } else {
            throw IllegalArgumentException("Invalid token")
        }

    }

    override suspend fun findUserByEmail(email: String): User? = userService.readByEmail(email)

    override suspend fun findUserByUserName(userName: String): User? = userService.readByUserName(userName)

    override suspend fun findTokenByUserId(userId: Int): String? = userService.readTokenByUserId(userId = userId)

    override suspend fun isTokenValid(token: String): Boolean {
        return try {
            // Verify the token without throwing an exception
            JWT.decode(token)
            val tok = token.removePrefix("Bearer ")
            transaction {
                UserService.Tokens.select { UserService.Tokens.token eq tok }.count() > 0L
                        && UserService.BlacklistedTokens.select { UserService.BlacklistedTokens.token eq tok }.count() == 0L
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun addToBlacklist(token: String) {
        // Implement the logic to add the token to the blacklist using Exposed
        transaction {
// delete token form Tokens table and add it to BlacklistedTokens table


            // Check if the token is already blacklisted
            if (UserService.BlacklistedTokens.select { UserService.BlacklistedTokens.token eq token }.count() == 0L) {
                val userId = UserService.Tokens.select { UserService.Tokens.token eq token }.singleOrNull()
                    ?.get(UserService.Tokens.userId)

                userId?.let {
                    // Delete the token from the Tokens table
                    UserService.Tokens.deleteWhere { UserService.Tokens.token eq token }

                    // Add the token to the BlacklistedTokens table
                    UserService.BlacklistedTokens.insert {
                        it[UserService.BlacklistedTokens.token] = token
                    }

                }
            }
        }
    }


    override suspend fun isTokenBlacklisted(existingToken: String): Boolean = transaction {
        UserService.BlacklistedTokens.select { UserService.BlacklistedTokens.token eq existingToken }.count() > 0L
    }

    override suspend fun saveToken(token: DbToken): Int =
        userService.createToken(userId = token.userId, token = token.token)
}

