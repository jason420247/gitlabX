package com.sozonov.gitlabx.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.sozonov.gitlabx.auth.AuthConstants.Companion.CLIENT_ID
import com.sozonov.gitlabx.auth.AuthConstants.Companion.ENDPOINT
import com.sozonov.gitlabx.auth.AuthConstants.Companion.REDIRECT_URI
import com.sozonov.gitlabx.auth.store.*
import com.sozonov.gitlabx.navigation.IDestination
import com.sozonov.gitlabx.navigation.Navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.*

typealias ActionWithValidToken = suspend (token: String) -> Boolean

class AuthService(context: Context) : AuthorizationService(context) {
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
        private suspend fun AuthService.performActionWithFreshTokens(
            authState: AuthState,
            producer: ProducerScope<Boolean>,
            action: ActionWithValidToken
        ) {
            authState.performActionWithFreshTokens(this) Refresh@{ accessToken,
                                                                   _,
                                                                   exc ->
                if (exc != null) {
                    producer.trySend(false)
                    Log.e(AUTH_TAG, exc.message ?: "error", exc)
                    return@Refresh
                }
                if (accessToken == null) {
                    producer.trySend(false)
                    Log.e(AUTH_TAG, "Access token is empty")
                    return@Refresh
                }
                producer.launch(Dispatchers.IO) {
                    producer.send(action.invoke(accessToken))
                }
            }
        }
    }

    suspend fun performWithActualToken(action: ActionWithValidToken): Boolean {
        val state = getState()

        if (state is EmptyAuthState) {
            return false
        }

        if (state is AuthStateAdapter) {
            val authState = state.state
            if (!authState.needsTokenRefresh) {
                if (authState.accessToken == null) {
                    return false
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

        return false
    }


    suspend fun logout() {
        when (getState()) {
            is SelfManagedAuthState -> {
                store.clear()
                Navigation.route(IDestination.SignInPopUp)
            }

            is AuthStateAdapter -> {
                store.clear()
                Navigation.route(IDestination.SignInPopUp)
            }

            EmptyAuthState -> return
        }
    }

    fun provideAuthIntent(): Intent = getAuthorizationRequestIntent(authRequest)
}