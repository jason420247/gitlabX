package com.sozonov.gitlabx.auth.store

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import org.json.JSONException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference

class AuthStateStore private constructor(private val context: Context) {

    private val mCurrentState = AtomicReference<IAuthState<*>>()
    private val Context.mPrefs: DataStore<Preferences> by preferencesDataStore(name = STORE_NAME)
    private val mMutex = Mutex()

    companion object {

        @JvmStatic
        private val TAG = "AuthStateStore"

        @JvmStatic
        private val STORE_NAME = "AuthState"

        @JvmStatic
        private val STORE_KEY = stringPreferencesKey(AuthStateStore::class.simpleName!!.hashCode().toString())

        @JvmStatic
        private val INSTANCE_REF: AtomicReference<WeakReference<AuthStateStore>> = AtomicReference(WeakReference(null))

        @JvmStatic
        fun getInstance(context: Context): AuthStateStore {
            var store = INSTANCE_REF.get().get()
            if (store == null) {
                store = AuthStateStore(context.applicationContext)
                INSTANCE_REF.set(WeakReference(store))
            }
            return store
        }
    }

    suspend fun getCurrent(): IAuthState<*> {
        var state = mCurrentState.get()
        if (state != null) {
            return state
        }

        state = readState()
        if (mCurrentState.compareAndSet(null, state)) {
            return state
        }

        return mCurrentState.get()
    }

    private suspend fun replace(state: IAuthState<*>): IAuthState<*> {
        writeState(state)
        mCurrentState.set(state)
        return state
    }

    suspend fun handleResponse(response: AuthorizationResponse?, ex: AuthorizationException?): AuthState {
        var current = (getCurrent() as? AuthStateAdapter)?.state
        if (current == null) {
            current = AuthState()
        }
        current.update(response, ex)
        return (replace(AuthStateAdapter(current)) as AuthStateAdapter).state
    }

    suspend fun handleResponse(response: TokenResponse?, ex: AuthorizationException?): AuthState {
        var current = (getCurrent() as? AuthStateAdapter)?.state
        if (current == null) {
            current = AuthState()
        }
        current.update(response, ex)
        return (replace(AuthStateAdapter(current)) as AuthStateAdapter).state
    }

    private suspend fun readState(): IAuthState<*> {
        return mMutex.withLock {
            try {
                val stateJson = context.mPrefs.data.map { preferences -> preferences[STORE_KEY] }.first()
                    ?: return@withLock EmptyAuthState
                try {
                    return@withLock Json.decodeFromString<SelfManagedAuthState>(stateJson)
                } catch (exc: SerializationException) {
                    try {
                        AuthStateAdapter(AuthState.jsonDeserialize(stateJson))
                    } catch (exc: JSONException) {
                        Log.e(TAG, exc.message, exc)
                        EmptyAuthState
                    }
                } catch (exc: IllegalArgumentException) {
                    Log.e(TAG, exc.message, exc)
                    EmptyAuthState
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                EmptyAuthState
            }
        }
    }

    @Throws(Exception::class)
    private suspend fun writeState(state: IAuthState<*>?) {
        mMutex.withLock {
            try {
                context.mPrefs.edit { settings ->
                    if (state == null) {
                        settings.remove(STORE_KEY)
                        return@edit
                    }
                    settings[STORE_KEY] = state.getJson()
                }

            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                throw e
            }
        }
    }
}
