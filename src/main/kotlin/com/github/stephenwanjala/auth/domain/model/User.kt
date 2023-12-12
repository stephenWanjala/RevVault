package com.github.stephenwanjala.auth.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val userName:String,
    val email:String,
    val password: String,
    val fullName: String? = null,
    val phoneNumber: String?=null,
    val salt: String,
    val id:Int=0,
)
