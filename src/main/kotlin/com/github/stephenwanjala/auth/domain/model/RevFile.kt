package com.github.stephenwanjala.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RevFile(
    val fileName: String,
    val fileType:String,
    val userId: Int,
    val filePath:String,
    val id:Int,
)
