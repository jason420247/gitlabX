package com.sozonov.gitlabx.auth.store

import net.openid.appauth.AuthState

class AuthStateAdapter(override val state: AuthState) : IAuthState<AuthState> {

    override fun getJson(): String = state.jsonSerializeString()!!
}