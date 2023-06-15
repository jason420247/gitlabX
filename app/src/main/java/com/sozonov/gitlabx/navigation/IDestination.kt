package com.sozonov.gitlabx.navigation

sealed interface IDestination<TData> {
    val route: String
    val data: TData?

    companion object {
        val SignInPopUp = PopUpTo<Unit>(Navigation.Routes.SIGN_IN)
        val SelfManagedSignIn = Destination<Unit>(Navigation.Routes.SELF_MANAGED_SIGN_IN)
    }
}

class Destination<TData>(override val route: String, override val data: TData? = null) : IDestination<TData> {
    override fun toString(): String = "route: $route; data: ${data != null}"
}

class PopUpTo<TData>(
    override val route: String,
    val popUpRoute: String = route,
    override val data: TData? = null,
    val inclusive: Boolean = true,
    val launchSingleTop: Boolean = false
) : IDestination<TData> {
    override fun toString(): String =
        "route: $route; popUpRoute: $popUpRoute; data: ${data != null}; inclusive: $inclusive; launchSingleTop: $launchSingleTop"
}