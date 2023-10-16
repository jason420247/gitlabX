package com.sozonov.gitlabx.auth

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.sozonov.gitlabx.auth.AuthConstants.Companion.CLIENT_ID
import com.sozonov.gitlabx.auth.AuthConstants.Companion.ENDPOINT
import com.sozonov.gitlabx.auth.AuthConstants.Companion.REDIRECT_URI
import com.sozonov.gitlabx.auth.store.AuthStateStore
import com.sozonov.gitlabx.auth.store.CloudAuthState
import com.sozonov.gitlabx.auth.store.IAuthState
import com.sozonov.gitlabx.auth.store.SelfManagedAuthState
import com.sozonov.gitlabx.navigation.Destination
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.user.dal.IUserCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class AuthService(context: Application, private val userCache: IUserCache) :
    AuthorizationService(context) {
    val store = AuthStateStore.getInstance(context.applicationContext)
    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse("${ENDPOINT}authorize"),
        Uri.parse("${ENDPOINT}token")
    )
    private val authRequestBuilder = AuthorizationRequest.Builder(
        serviceConfig,
        CLIENT_ID,
        ResponseTypeValues.CODE,
        Uri.parse(REDIRECT_URI)
    )

    private val authRequest =
        authRequestBuilder.setScopes("openid", "email", "profile", "api").build()

    suspend fun getState() = withContext(Dispatchers.IO) { store.getCurrent() }
    suspend fun saveState(state: IAuthState<*>) = store.replace(state)

    companion object {
        const val AUTH_TAG = "auth"

        @JvmStatic
        private suspend fun <TResult> AuthService.performActionWithFreshTokens(
            authState: AuthState,
            producer: ProducerScope<TResult?>,
            action: suspend (accessToken: String?, refreshToken: String?) -> TResult
        ) {
            val service = this
            authState.performActionWithFreshTokens(service) { accessToken,
                                                              refreshToken,
                                                              exc ->
                producer.launch {
                    if (exc != null) {
                        producer.send(null)
                        Log.e(AUTH_TAG, exc.message ?: "error", exc)
                    }
                    if (accessToken == null) {
                        producer.send(null)
                        Log.e(AUTH_TAG, "Access token is empty")
                    }
                    producer.send(action.invoke(accessToken, refreshToken))
                }

            }
        }
    }

    suspend fun <TResult> performWithActualToken(action: suspend (accessToken: String?, refreshToken: String?) -> TResult): TResult? {
        val state = getState()

        if (state is CloudAuthState) {
            val authState = state.state
            if (!authState.needsTokenRefresh) {
                if (authState.accessToken == null) {
                    return null
                }
                return action.invoke(authState.accessToken, authState.refreshToken)
            }
            val perform = callbackFlow {
                performActionWithFreshTokens(authState, this, action)
                awaitClose()
            }
            return perform.first()
        }

        if (state is SelfManagedAuthState) {
            return action.invoke(state.accessToken, null)
        }

        return null
    }

    suspend fun logout() {
        when (getState()) {
            is SelfManagedAuthState -> {
                store.clear()
            }

            is CloudAuthState -> {
                store.clear()
            }
        }
        userCache.deleteUser()
        withContext(Dispatchers.Main) {
            Navigation.destination =
                Destination(Navigation.Routes.SIGN_IN, popUpRoute = Navigation.Routes.SIGN_IN)
        }
    }

    fun provideAuthIntent(): Intent = getAuthorizationRequestIntent(authRequest)
}