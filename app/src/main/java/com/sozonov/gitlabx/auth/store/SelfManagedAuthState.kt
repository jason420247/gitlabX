package com.sozonov.gitlabx.auth.store

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class SelfManagedAuthState(val server: String, val accessToken: String) : IAuthState<SelfManagedAuthState> {
    override val state: SelfManagedAuthState
        get() = this

    override fun getJson(): String = Json.encodeToString(state)
}
