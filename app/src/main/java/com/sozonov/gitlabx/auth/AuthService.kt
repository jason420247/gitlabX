package com.sozonov.gitlabx.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sozonov.gitlabx.auth.AuthProps.Companion.CLIENT_ID
import com.sozonov.gitlabx.auth.AuthProps.Companion.ENDPOINT
import com.sozonov.gitlabx.auth.AuthProps.Companion.REDIRECT_URI
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class AuthService(context: Context) : AuthorizationService(context) {
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


    fun refreshToken() {
        // todo
    }

    fun revokeToken() {
        // todo
    }

    fun provideAuthIntent(): Intent = getAuthorizationRequestIntent(authRequest)
}