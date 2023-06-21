package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.auth.AuthService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpClient = module {
    single {
        val authService = AuthService(get())
        val client = HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(json = Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { exception, _ ->
                    val clientException =
                        exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                    val exceptionResponse = clientException.response
                    if (exceptionResponse.status == HttpStatusCode.Unauthorized) {
                        authService.logout()
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
                retryIf { _, response ->
                    !response.status.isSuccess() || response.status != HttpStatusCode.NotFound || response.status != HttpStatusCode.Unauthorized
                }
                exponentialDelay()
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }
        client
    }
}