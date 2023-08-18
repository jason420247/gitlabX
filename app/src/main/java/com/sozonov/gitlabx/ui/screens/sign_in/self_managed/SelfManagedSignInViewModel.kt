package com.sozonov.gitlabx.ui.screens.sign_in.self_managed

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.store.SelfManagedAuthState
import com.sozonov.gitlabx.ui.screens.sign_in.UserState
import com.sozonov.gitlabx.user.IUserRepository
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelfManagedSignInViewModel(
    private val mAuthService: AuthService,
    private val userRepository: IUserRepository
) : ViewModel() {
    var userState by mutableStateOf(UserState())
        private set

    fun fetchUser(server: String, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val state = SelfManagedAuthState(server, token)
                mAuthService.saveState(state)
                Log.i(AuthService.AUTH_TAG, "auth tokens saved")
                fetchUser()
            } catch (e: IOException) {
                produceUserError("Failed fetch user")
                Log.e(AuthService.AUTH_TAG, e.message, e)
            } catch (e: Exception) {
                produceUserError(e.message ?: "Unknown error")
                Log.e(AuthService.AUTH_TAG, e.message, e)
            }
        }
    }

    private suspend fun fetchUser() {
        val user = userRepository.fetchUser()
        withContext(Dispatchers.Main) {
            userState = UserState(user.id)
        }
    }

    private fun produceUserError(error: String) {
        userState = UserState(null, error)
    }
}