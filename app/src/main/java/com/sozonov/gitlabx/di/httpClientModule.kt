package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.snackbar.Snackbar
import com.sozonov.gitlabx.snackbar.SnackbarData
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpClient = module {

    single {
        val authService = get<AuthService>()
        val client = HttpClient(Android) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(json = Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            Logging {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->
                    val clientException =
                        exception as? ClientRequestException
                            ?: return@handleResponseExceptionWithRequest
                    val exceptionResponse = clientException.response
                    if (exceptionResponse.status == HttpStatusCode.Unauthorized) {
                        authService.logout()
                        withContext(Dispatchers.Main) {
                            Snackbar.show = SnackbarData("Unauthorized. Please sign in again")
                        }
                    }
                }
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        authService.performWithActualToken { token ->
                            BearerTokens(token, "")
                        }
                    }
                }
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                retryIf { _, response ->
                    if (response.status.isSuccess()) {
                        return@retryIf false
                    }
                    if (response.status == HttpStatusCode.NotFound || response.status == HttpStatusCode.Unauthorized) {
                        return@retryIf false
                    }
                    true
                }
                exponentialDelay()
            }
        }
        client
    }
}