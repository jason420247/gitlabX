package com.sozonov.gitlabx.user

sealed class UserState{
    data object UserCreated : UserState()
    data object UserDeleted : UserState()
}