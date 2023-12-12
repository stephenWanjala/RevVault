package com.github.stephenwanjala.auth.domain.repository

import com.github.stephenwanjala.auth.domain.model.User

interface AuthRepository {

    suspend fun signUp(user: User): Boolean

    suspend fun signOut()

    suspend fun findUserByEmail(email: String): User?

    suspend fun findUserByUserName(userName: String): User?
}