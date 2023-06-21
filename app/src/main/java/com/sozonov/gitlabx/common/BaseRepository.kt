package com.sozonov.gitlabx.common

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.utils.httpClient.HttpClientWithBasePath
import io.ktor.client.*

abstract class BaseRepository(private val httpClient: HttpClient, private val authService: AuthService) {
    private var mClient: HttpClientWithBasePath? = null

    protected suspend fun provideClient() = mClient ?: HttpClientWithBasePath.Create(httpClient, authService)
}