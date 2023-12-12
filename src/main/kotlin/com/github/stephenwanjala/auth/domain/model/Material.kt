package com.github.stephenwanjala.auth.domain.model

data class Material(
    val id: Int,
    val type:MaterialType,
    val subject: Subject,
    val userId: Int,
    val file :Int
)
