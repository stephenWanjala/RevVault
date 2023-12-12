package com.github.stephenwanjala.auth.security.token

data class TokenConfig(
    val issuer: String,
    val audience: String,
    val expiresIn: Long,
    val secrete: String
)
