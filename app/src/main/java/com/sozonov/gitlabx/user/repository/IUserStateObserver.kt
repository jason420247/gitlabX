package com.sozonov.gitlabx.user.repository

import com.sozonov.gitlabx.user.model.UserState
import kotlinx.coroutines.flow.Flow

interface IUserStateObserver{
    val observeCreation: Flow<UserState.UserCreated>
    val observeDeletion: Flow<UserState.UserDeleted>
}