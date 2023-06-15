package com.sozonov.gitlabx.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sozonov.gitlabx.auth.AuthConstants.Companion.CLIENT_ID
import com.sozonov.gitlabx.auth.AuthConstants.Companion.ENDPOINT
import com.sozonov.gitlabx.auth.AuthConstants.Companion.REDIRECT_URI
import com.sozonov.gitlabx.auth.store.*
import com.sozonov.gitlabx.navigation.IDestination
import com.sozonov.gitlabx.navigation.Navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

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

    val state get() = flow { emit(store.getCurrent()) }.flowOn(Dispatchers.IO)


    companion object {
        const val AUTH_TAG = "auth"
    }


    suspend fun saveState(state: IAuthState<*>) = store.replace(state)

    fun refreshToken() {
        // refresh
    }

    suspend fun logout() {
        when (state.first()) {
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