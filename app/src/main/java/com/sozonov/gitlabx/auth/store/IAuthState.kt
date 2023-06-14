package com.sozonov.gitlabx.auth.store

sealed interface IAuthState<TState> {
    val state: TState
    fun getJson(): String

}

object EmptyAuthState : IAuthState<Unit> {
    override val state: Unit
        get() = Unit

    override fun getJson(): String = ""
}