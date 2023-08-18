package com.sozonov.gitlabx.ui.screens.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sozonov.gitlabx.user.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(private val userRepository: IUserRepository) : ViewModel() {
    var userState by mutableStateOf(UserState())
        private set;

    fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userRepository.fetchUser()
                withContext(Dispatchers.Main) {
                    userState = UserState(user.id);
                }
            } catch (e: Exception) {
                userState = UserState(errorMessage = e.message)
            }
        }
    }

    fun produceUserError(error: String) {
        userState = UserState(null, error)
    }
}

