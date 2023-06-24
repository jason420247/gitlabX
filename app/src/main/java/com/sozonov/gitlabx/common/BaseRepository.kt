package com.sozonov.gitlabx.common

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.utils.httpClient.HttpClientWithBasePathBuilder
import io.ktor.client.*

abstract class BaseRepository(private val httpClient: HttpClient, private val authService: AuthService) {

    protected suspend fun provideApiClient() = HttpClientWithBasePathBuilder.buildApiClient(httpClient, authService)
}