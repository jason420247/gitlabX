package com.sozonov.gitlabx.user

class UserCacheImpl : IUserCache {
    override suspend fun saveUser(userModel: UserModel) {
        return
    }

    override suspend fun getUser(): UserModel? {
        return null
    }
}