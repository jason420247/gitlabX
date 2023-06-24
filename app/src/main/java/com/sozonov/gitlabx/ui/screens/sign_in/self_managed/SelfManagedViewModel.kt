package com.sozonov.gitlabx.ui.screens.sign_in.self_managed

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.store.SelfManagedAuthState
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.navigation.PopUpTo
import com.sozonov.gitlabx.user.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelfManagedViewModel(private val mAuthService: AuthService, private val userRepository: IUserRepository) :
    ViewModel() {

    var server = mutableStateOf("")
    var accessToken = mutableStateOf("")

    fun saveAuthState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val state = SelfManagedAuthState(server.value, accessToken.value)
                mAuthService.saveState(state)
                Log.i(AuthService.AUTH_TAG, "auth tokens saved")
                fetchUserAndGoToWelcomeView()
            } catch (e: Exception) {
                Log.e(AuthService.AUTH_TAG, e.message, e)
            }
        }
    }

    private suspend fun fetchUserAndGoToWelcomeView() {
        userRepository.fetchUser()
        Navigation.route(PopUpTo<Unit>(Navigation.Routes.WELCOME))
    }
}