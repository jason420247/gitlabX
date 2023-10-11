package com.sozonov.gitlabx.navigation

import android.os.Parcelable
import androidx.compose.runtime.Immutable

@Immutable
data class Destination(
    val route: String,
    val popUpRoute: Boolean = false,
    val inclusive: Boolean = true,
    val launchSingleTop: Boolean = false,
    val data: Parcelable? = null
) {
    override fun toString(): String =
        "route: $route; popUpRoute: $popUpRoute; data: ${data != null}; inclusive: $inclusive; launchSingleTop: $launchSingleTop"
}