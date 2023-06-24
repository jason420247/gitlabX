package com.sozonov.gitlabx.user

interface IUserCache {
    suspend fun saveUser(userModel: UserModel)
    suspend fun getUser(): UserModel?

    suspend fun deleteUser()
}