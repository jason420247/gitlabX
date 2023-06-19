package com.sozonov.gitlabx.di

import com.sozonov.gitlabx.auth.AuthService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.dsl.module

val httpClient = module {
    single {
        val authService = AuthService(get())
        val client = HttpClient(CIO) {
            install(ContentNegotiation) { json() }
            expectSuccess = true
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
            install(HttpRequestRetry) {
                retryIf { _, response ->
                    !response.status.isSuccess() || response.status != HttpStatusCode.Unauthorized || response.status != HttpStatusCode.NotFound
                }
                exponentialDelay()
            }
            install(Logging)
        }
        client.plugin(HttpSend).intercept { request ->
            val original = execute(request)
            val result = authService.performWithActualToken { token ->
                request.bearerAuth(token)
                execute(request)
            }
            result ?: original
        }
        client
    }
}