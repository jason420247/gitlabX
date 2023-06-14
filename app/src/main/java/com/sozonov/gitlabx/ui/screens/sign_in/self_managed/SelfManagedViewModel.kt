package com.sozonov.gitlabx.ui.screens.sign_in.self_managed

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sozonov.gitlabx.auth.store.SelfManagedAuthState
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.navigation.PopUpTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelfManagedViewModel : ViewModel() {
    var server = mutableStateOf("")
    var accessToken = mutableStateOf("")

    fun saveAuthState() {
        val state = SelfManagedAuthState(server.value, accessToken.value)
        viewModelScope.launch(Dispatchers.IO) {
            // save
            withContext(Dispatchers.Main) {
                Navigation.route(PopUpTo<Unit>(Navigation.Routes.SIGN_IN, popUpRoute = Navigation.Routes.SIGN_IN))
            }
        }

    }
}