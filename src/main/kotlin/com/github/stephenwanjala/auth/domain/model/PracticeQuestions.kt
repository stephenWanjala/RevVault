package com.github.stephenwanjala.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PracticeQuestion(
    val id: Int,
    val subject: Subject,
    val user: User,
    val file: RevFile
)
