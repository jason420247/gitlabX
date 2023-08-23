package com.sozonov.gitlabx.ui.screens.sign_in.cloud

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.ui.screens.sign_in.UserState
import com.sozonov.gitlabx.user.IUserRepository
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CloudSignInViewModel(private val userRepository: IUserRepository) : ViewModel() {
    var userState by mutableStateOf(UserState())
        private set

    fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userRepository.fetchUser()
                withContext(Dispatchers.Main) {
                    userState = UserState(user.id, user.fullName)
                }
            } catch (e: IOException) {
                produceUserError("Failed fetch user")
                Log.e(AuthService.AUTH_TAG, e.message, e)
            } catch (e: Exception) {
                produceUserError(e.message ?: "Unknown error")
                Log.e(AuthService.AUTH_TAG, e.message, e)
            }
        }
    }

    fun produceUserError(error: String) {
        userState = UserState(errorMessage = error)
    }
}

