package com.github.stephenwanjala.auth.data.repository

import com.github.stephenwanjala.auth.domain.model.User
import com.github.stephenwanjala.auth.domain.repository.AuthRepository
import com.github.stephenwanjala.auth.data.database.UserService

class AuthRepositoryImpl(
    private val userService: UserService
) : AuthRepository {
    override suspend fun signUp(user: User): Int = userService.create(user)

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }

    override suspend fun findUserByEmail(email: String): User? = userService.readByEmail(email)

    override suspend fun findUserByUserName(userName: String): User? = userService.readByUserName(userName)

}