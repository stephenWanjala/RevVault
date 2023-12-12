package com.github.stephenwanjala.auth.domain.model

import kotlinx.serialization.Serializable
@Serializable
data class Subject(
    val subjectName:String,
    val id:Int,
)
