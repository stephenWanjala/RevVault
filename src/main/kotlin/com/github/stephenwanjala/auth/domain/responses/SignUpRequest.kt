package com.github.stephenwanjala.auth.domain.responses

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val authInfo: AuthInfo,
    val fullName:String?=null,
    val phoneNumber:String?=null,
){
    init {
        require(authInfo.email.isNotBlank()){"Email must not be blank"}
        require(authInfo.password.isNotBlank()){"Password must not be blank"}
        require(authInfo.userName.isNotBlank()){"Username must not be blank"}
    }
}