package com.sozonov.gitlabx.user

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.common.BaseRepository
import io.ktor.client.*
import io.ktor.client.call.*

class UserRepositoryImpl(private val userCache: IUserCache, httpClient: HttpClient, authService: AuthService) :
    IUserRepository, BaseRepository(httpClient, authService) {

    private val resourcePath = "/users"

    override suspend fun getUser(id: Int): UserModel {
        try {
            return userCache.getUser() ?: provideClient().get("$resourcePath/$id").body()
        } catch (e: Exception) {
            throw e
        }

    }
}
