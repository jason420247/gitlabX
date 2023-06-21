package com.sozonov.gitlabx.user

interface IUserRepository {
    suspend fun getUser(id: Int): UserModel
}