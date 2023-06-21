package com.sozonov.gitlabx.ui.screens.sign_in

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {

    private val _gitlabCloudAuthProcessing = mutableStateOf(false)
    val gitlabCloudAuthProcessing: State<Boolean> = _gitlabCloudAuthProcessing

    fun changeGitlabCloudAuthProcessing(state: Boolean) {
        _gitlabCloudAuthProcessing.value = state
    }
}