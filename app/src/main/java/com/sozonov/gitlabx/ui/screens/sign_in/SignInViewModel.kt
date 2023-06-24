package com.sozonov.gitlabx.ui.screens.sign_in

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sozonov.gitlabx.navigation.Navigation
import com.sozonov.gitlabx.navigation.PopUpTo
import com.sozonov.gitlabx.user.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(private val userRepository: IUserRepository) : ViewModel() {

    private val _gitlabCloudAuthProcessing = mutableStateOf(false)
    val gitlabCloudAuthProcessing: State<Boolean> = _gitlabCloudAuthProcessing

    fun changeGitlabCloudAuthProcessing(state: Boolean) {
        _gitlabCloudAuthProcessing.value = state
    }

    fun fetchUserAndGoToWelcomeView() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.fetchUser()
            withContext(Dispatchers.Main) {
                changeGitlabCloudAuthProcessing(false)
            }
            Navigation.route(PopUpTo<Unit>(Navigation.Routes.WELCOME))
        }
    }
}