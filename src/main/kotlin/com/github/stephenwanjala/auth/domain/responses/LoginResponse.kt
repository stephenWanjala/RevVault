package com.github.stephenwanjala.auth.domain.responses

import kotlinx.serialization.Serializable
@Serializable
data class LoginResponse(
    val token: String,
)
