package com.sozonov.gitlabx.user.model

sealed class UserState{
    data object UserCreated : UserState()
    data object UserDeleted : UserState()
}