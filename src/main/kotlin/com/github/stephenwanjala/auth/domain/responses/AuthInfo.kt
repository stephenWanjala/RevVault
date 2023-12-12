package com.github.stephenwanjala.auth.domain.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthInfo(
    val userName:String,
    val email: String,
    val password: String,
)