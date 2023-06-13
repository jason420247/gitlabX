package com.sozonov.gitlabx.navigation

sealed interface IDestination<TData> {
    val route: String
    val data: TData?
}

class Destination<TData>(override val route: String, override val data: TData? = null) : IDestination<TData> {
    override fun toString(): String = "route: $route; data: ${data != null}"
}

class PopUpTo<TData>(
    override val route: String,
    val popUpRoute: String? = null,
    override val data: TData? = null,
    val inclusive: Boolean = true,
    val launchSingleTop: Boolean = false
) : IDestination<TData> {
    override fun toString(): String =
        "route: $route; popUpRoute: $popUpRoute; data: ${data != null}; inclusive: $inclusive; launchSingleTop: $launchSingleTop"
}