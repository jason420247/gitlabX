package com.sozonov.gitlabx.user

interface IUserRepository {
    suspend fun getUser(): UserModel

    suspend fun fetchUser(): UserModel

    suspend fun saveUser(userModel: UserModel)

    suspend fun deleteUser()
}