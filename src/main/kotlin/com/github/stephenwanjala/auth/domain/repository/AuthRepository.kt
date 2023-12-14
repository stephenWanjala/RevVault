package com.github.stephenwanjala.auth.domain.repository

import com.github.stephenwanjala.auth.domain.model.DbToken
import com.github.stephenwanjala.auth.domain.model.User

interface AuthRepository {

    suspend fun signUp(user: User):Int

    @Throws(IllegalArgumentException::class)
    suspend fun signOut(token: String)

    suspend fun findUserByEmail(email: String): User?

    suspend fun findUserByUserName(userName: String): User?

    suspend fun findTokenByUserId(userId: Int): String?

    suspend fun isTokenValid(token: String): Boolean

    suspend fun isTokenBlacklisted(existingToken: String): Boolean

    suspend fun saveToken(token:DbToken):Int
}