package com.sozonov.gitlabx.utils.httpClient

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.store.CloudAuthState
import com.sozonov.gitlabx.auth.store.SelfManagedAuthState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest

class HttpClientWithBasePathBuilder private constructor() {

    companion object {
        private const val apiPath = "api/"
        private const val versionPath = "v4/"

        @JvmStatic
        suspend fun buildApiClient(httpClient: HttpClient, authService: AuthService): HttpClient {
            val authState = authService.getState()
            val baseUrl = StringBuilder()
            if (authState is CloudAuthState) {
                baseUrl.append("https://gitlab.com/")
            }
            if (authState is SelfManagedAuthState) {
                baseUrl.append(authState.server).also {
                    if (authState.server.last() != '/') {
                        it.append("/")
                    }
                }
            }
            baseUrl.append(apiPath).append(versionPath)
            return httpClient.config {
                defaultRequest {
                    url(baseUrl.toString())
                }
            }
        }
    }
}