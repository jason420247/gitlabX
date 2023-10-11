package com.sozonov.gitlabx.user

import kotlinx.coroutines.flow.Flow

interface IUserStateObserver{
    val observeCreation: Flow<UserState.UserCreated>
    val observeDeletion: Flow<UserState.UserDeleted>
}