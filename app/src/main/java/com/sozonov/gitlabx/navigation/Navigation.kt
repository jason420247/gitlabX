package com.sozonov.gitlabx.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Navigation {
    companion object {

        const val TAG = "NAVIGATION"
        private val mDestination: MutableStateFlow<IDestination<*>?> =
            MutableStateFlow(null)

        val destination = mDestination.asStateFlow()
        fun route(destination: IDestination<*>?) {
            mDestination.tryEmit(destination)
        }
    }

    interface Routes {
        companion object {
            const val SIGN_IN = "SIGN_IN"
            const val SELF_MANAGED_SIGN_IN = "SELF_MANAGED_SIGN_IN"
            const val WELCOME = "WELCOME"
        }
    }
}