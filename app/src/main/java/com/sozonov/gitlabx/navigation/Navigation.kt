package com.sozonov.gitlabx.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Navigation {
    companion object {

        const val TAG = "NAVIGATION"
        private val mNext: MutableStateFlow<Destination<*>?> =
            MutableStateFlow(null)
        private val mPopUpTo: MutableStateFlow<PopUpTo<*>?> =
            MutableStateFlow(null)
        val next = mNext.asStateFlow()
        val popUpTo = mPopUpTo.asStateFlow()
        fun next(destination: Destination<*>?) {
            mNext.tryEmit(destination)
        }

        fun popUpTo(popUpTo: PopUpTo<*>?) {
            mPopUpTo.tryEmit(popUpTo)
        }
    }

    interface Routes {
        companion object {
            const val SIGN_IN = "SIGN_IN"
            const val SELF_MANAGED_SIGN_IN = "SELF_MANAGED_SIGN_IN"
        }
    }
}

class Destination<TData>(val route: String, val data: TData? = null) {
    override fun toString(): String = "route: $route; data: ${data != null}"
}

class PopUpTo<TData>(
    val route: String,
    val popUpRoute: String? = null,
    val data: TData? = null,
    val inclusive: Boolean = true,
    val launchSingleTop: Boolean = false
) {
    override fun toString(): String =
        "route: $route; popUpRoute: $popUpRoute; data: ${data != null}; inclusive: $inclusive; launchSingleTop: $launchSingleTop"
}