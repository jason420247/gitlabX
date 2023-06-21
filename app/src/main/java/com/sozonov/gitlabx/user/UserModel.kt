package com.sozonov.gitlabx.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: Int,
    val username: String,
    val name: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("public_email")
    val publicEmail: String
)
