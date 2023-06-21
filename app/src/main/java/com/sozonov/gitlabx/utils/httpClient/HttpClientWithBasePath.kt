package com.sozonov.gitlabx.utils.httpClient

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.store.AuthStateAdapter
import com.sozonov.gitlabx.auth.store.SelfManagedAuthState
import io.ktor.client.*
import io.ktor.client.request.*

class HttpClientWithBasePath private constructor(private val httpClient: HttpClient, private val baseUrl: String) {

    suspend fun get(
        resourcePath: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ) = httpClient.get { url(StringBuilder(baseUrl).append(resourcePath).toString()); block() }

    companion object {
        private const val apiPath = "/api"
        private const val versionPath = "/v4"

        @JvmStatic
        suspend fun Create(httpClient: HttpClient, authService: AuthService): HttpClientWithBasePath {
            val authState = authService.getState()
            val baseUrl = StringBuilder()

            if (authState is AuthStateAdapter) {
                baseUrl.append("https://gitlab.com")
            }
            if (authState is SelfManagedAuthState) {
                baseUrl.append(authState.server)
            }

            baseUrl.append(apiPath).append(versionPath)

            return HttpClientWithBasePath(httpClient, baseUrl.toString())
        }
    }
}