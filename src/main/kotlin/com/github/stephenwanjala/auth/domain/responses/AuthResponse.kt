package com.github.stephenwanjala.auth.domain.responses

data class AuthResponse(
    val token: String,
    val userName: String,
    val email: String,
    val fullName: String? = null,
    val phoneNumber: String,
)
