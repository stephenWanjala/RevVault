package com.github.stephenwanjala.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PastPaper(
    val id: Int,
    val subject: Subject,
    val userId: Int,
    val fileId :Int
)
