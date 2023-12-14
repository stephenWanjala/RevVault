package com.github.stephenwanjala.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DbToken(
    val token: String,
    val userId: Int
)