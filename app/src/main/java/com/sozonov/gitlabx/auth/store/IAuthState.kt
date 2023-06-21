package com.sozonov.gitlabx.auth.store

sealed interface IAuthState<TState> {
    val state: TState
    fun getJson(): String

}