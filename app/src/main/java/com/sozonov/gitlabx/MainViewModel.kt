package com.sozonov.gitlabx

import androidx.lifecycle.ViewModel
import com.sozonov.gitlabx.user.IUserRepository
import com.sozonov.gitlabx.user.IUserStateObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class MainViewModel(userStateObserver: IUserStateObserver, userRepository: IUserRepository) :
    ViewModel() {

    val userCreated = userStateObserver.observeCreation.map { _ -> userRepository.getUser() }
        .flowOn(Dispatchers.IO)
    val userDeleted = userStateObserver.observeDeletion.flowOn(Dispatchers.IO)


}