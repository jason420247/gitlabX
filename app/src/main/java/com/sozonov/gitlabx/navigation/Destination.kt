package com.sozonov.gitlabx.navigation

import android.os.Parcelable
import androidx.compose.runtime.Immutable

@Immutable
class Destination(
    val route: String,
    val popUpRoute: String? = null,
    val inclusive: Boolean = true,
    val launchSingleTop: Boolean = true,
    val data: Parcelable? = null
) {
    override fun toString(): String =
        "route: $route; popUpRoute: $popUpRoute; data: ${data != null}; inclusive: $inclusive; launchSingleTop: $launchSingleTop"
}