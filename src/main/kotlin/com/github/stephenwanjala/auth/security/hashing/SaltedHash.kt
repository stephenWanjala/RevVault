package com.github.stephenwanjala.auth.security.hashing

data class SaltedHash(
    val hash:String,
    val salt:String
)
