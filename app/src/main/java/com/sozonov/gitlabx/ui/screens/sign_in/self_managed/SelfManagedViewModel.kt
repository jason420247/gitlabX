package com.sozonov.gitlabx.ui.screens.sign_in.self_managed

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sozonov.gitlabx.auth.AuthService
import com.sozonov.gitlabx.auth.store.SelfManagedAuthState
import com.sozonov.gitlabx.navigation.IDestination.Companion.SignInPopUp
import com.sozonov.gitlabx.navigation.Navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelfManagedViewModel(private val mAuthService: AuthService) : ViewModel() {

    var server = mutableStateOf("")
    var accessToken = mutableStateOf("")

    fun saveAuthState() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val state = SelfManagedAuthState(server.value, accessToken.value)
                mAuthService.saveState(state)
                Log.i(AuthService.AUTH_TAG, "auth tokens saved")
                withContext(Dispatchers.Main) {
                    Navigation.route(SignInPopUp)
                }
            } catch (e: Exception) {
                Log.e(AuthService.AUTH_TAG, e.message, e)
            }
        }
    }
}