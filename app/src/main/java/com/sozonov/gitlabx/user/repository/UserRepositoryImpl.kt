package com.sozonov.gitlabx.user.repository

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.common.BaseRepository
import com.sozonov.gitlabx.user.dal.IUserCache
import com.sozonov.gitlabx.user.model.UserModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class UserRepositoryImpl(
    private val userCache: IUserCache,
    httpClient: HttpClient,
    authService: AuthService
) : IUserRepository, BaseRepository(httpClient, authService) {

    @Volatile
    private var mUser: UserModel? = null

    override suspend fun getUser(): UserModel = withContext(Dispatchers.IO) {
        if (mUser != null) {
            mUser as UserModel
        }
        val cacheUser = userCache.getUser()
        if (cacheUser != null)
            return@withContext cacheUser
        fetchUser()
    }

    override suspend fun fetchUser(): UserModel = withContext(Dispatchers.IO) {
        val remoteUser = provideApiClient().get("user").body<UserModel>()
        saveUser(remoteUser)
        remoteUser
    }

    override suspend fun saveUser(userModel: UserModel) = withContext(Dispatchers.IO) {
        deleteUser()
        userCache.saveUser(userModel)
        mUser = userModel
    }

    override suspend fun deleteUser() = withContext(Dispatchers.IO) {
        userCache.deleteUser()
    }
}
