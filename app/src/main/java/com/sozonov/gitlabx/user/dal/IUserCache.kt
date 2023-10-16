package com.sozonov.gitlabx.user.dal

import com.sozonov.gitlabx.user.model.UserModel

interface IUserCache {
    suspend fun saveUser(userModel: UserModel)
    suspend fun getUser(): UserModel?
    suspend fun deleteUser()
}
