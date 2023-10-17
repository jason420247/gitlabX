package com.sozonov.gitlabx.welcome

import androidx.compose.runtime.mutableStateOf
import com.sozonov.gitlabx.user.model.UserModel
import com.sozonov.gitlabx.user.repository.IUserRepository
import com.sozonov.gitlabx.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeViewModel(private val userRepository: IUserRepository) : BaseViewModel() {
    val user = mutableStateOf<UserModel?>(null)

    init {
        viewModelScopeWithCEH.launch {
            val u = userRepository.getUser()
            delay(1000)
            user.value = u
        }
    }
}