package com.sozonov.gitlabx.user

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.common.BaseRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class UserRepositoryImpl(private val userCache: IUserCache, httpClient: HttpClient, authService: AuthService) :
    IUserRepository, BaseRepository(httpClient, authService) {

    @Volatile
    private var mUser: UserModel? = null

    override suspend fun getUser(): UserModel {
        try {
            if (mUser != null) {
                return mUser as UserModel
            }
            val cacheUser = userCache.getUser()
            if (cacheUser != null)
                return cacheUser
            return fetchUser()
        } catch (e: Exception) {
            throw e
        }

    }

    override suspend fun fetchUser(): UserModel {
        val remoteUser = provideApiClient().get("user").body<UserModel>()
        saveUser(remoteUser)
        return remoteUser
    }

    override suspend fun saveUser(userModel: UserModel) {
        deleteUser()
        userCache.saveUser(userModel)
        mUser = userModel
    }

    override suspend fun deleteUser() {
        userCache.deleteUser()
    }
}
