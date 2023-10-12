package com.sozonov.gitlabx.user.repository

import com.sozonov.gitlabx.user.model.UserModel

interface IUserRepository {
    suspend fun getUser(): UserModel

    suspend fun fetchUser(): UserModel

    suspend fun saveUser(userModel: UserModel)

    suspend fun deleteUser()
}