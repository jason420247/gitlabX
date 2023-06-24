package com.sozonov.gitlabx.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.sozonov.gitlabx.auth.AuthConstants.Companion.CLIENT_ID
import com.sozonov.gitlabx.auth.AuthConstants.Companion.ENDPOINT
import com.sozonov.gitlabx.auth.AuthConstants.Companion.REDIRECT_URI
import com.sozonov.gitlabx.auth.store.AuthStateAdapter
import com.sozonov.gitlabx.auth.store.AuthStateStore
import com.sozonov.gitlabx.auth.store.IAuthState
import com.sozonov.gitlabx.auth.store.SelfManagedAuthState
import com.sozonov.gitlabx.navigation.IDestination
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.user.IUserCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.*

class AuthService(context: Context, private val userCache: IUserCache) : AuthorizationService(context) {
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

    private val authRequest = authRequestBuilder.setScopes("openid", "email", "profile", "api", "read_user").build()

    suspend fun getState() = withContext(Dispatchers.IO) { store.getCurrent() }
    suspend fun saveState(state: IAuthState<*>) = store.replace(state)

    companion object {
        const val AUTH_TAG = "auth"

        @JvmStatic
        private suspend fun <TResult> AuthService.performActionWithFreshTokens(
            authState: AuthState,
            producer: ProducerScope<TResult?>,
            action: suspend (token: String) -> TResult
        ) {
            authState.performActionWithFreshTokens(this) Refresh@{ accessToken,
                                                                   _,
                                                                   exc ->
                if (exc != null) {
                    producer.trySend(null)
                    Log.e(AUTH_TAG, exc.message ?: "error", exc)
                    return@Refresh
                }
                if (accessToken == null) {
                    producer.trySend(null)
                    Log.e(AUTH_TAG, "Access token is empty")
                    return@Refresh
                }
                producer.launch(Dispatchers.IO) {
                    producer.send(action.invoke(accessToken))
                }
            }
        }
    }

    suspend fun <TResult> performWithActualToken(action: suspend (token: String) -> TResult): TResult? {
        val state = getState()

        if (state is AuthStateAdapter) {
            val authState = state.state
            if (!authState.needsTokenRefresh) {
                if (authState.accessToken == null) {
                    return null
                }
                return action.invoke(authState.accessToken!!)
            }
            val perform = callbackFlow {
                performActionWithFreshTokens(authState, this, action)
            }
            return perform.first()
        }

        if (state is SelfManagedAuthState) {
            return action.invoke(state.accessToken)
        }

        return null
    }

    suspend fun logout() {
        when (getState()) {
            is SelfManagedAuthState -> {
                store.clear()
            }

            is AuthStateAdapter -> {
                store.clear()
            }
        }
        userCache.deleteUser()
        Navigation.route(IDestination.SignInPopUp)
    }

    fun provideAuthIntent(): Intent = getAuthorizationRequestIntent(authRequest)
}