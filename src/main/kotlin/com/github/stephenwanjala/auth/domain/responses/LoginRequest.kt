package com.github.stephenwanjala.auth.domain.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
