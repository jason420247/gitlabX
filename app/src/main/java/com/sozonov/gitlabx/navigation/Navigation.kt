package com.sozonov.gitlabx.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.setValue

interface Navigation {
    companion object {

        const val TAG = "NAVIGATION"

        var destination by mutableStateOf<Destination?>(null, policy = neverEqualPolicy())
    }

    interface Routes {
        companion object {
            const val SIGN_IN = "SIGN_IN"
            const val SELF_MANAGED_SIGN_IN = "SELF_MANAGED_SIGN_IN"
            const val WELCOME = "WELCOME"
        }
    }
}