package com.thezayin.qrscanner.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.thezayin.values.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    data object Scan : BottomNavItem(
        route = "scan",
        label = R.string.scan,
        icon = R.drawable.ic_scan
    )

    data object Create : BottomNavItem(
        route = "create",
        label = R.string.create,
        icon = R.drawable.ic_add
    )

    data object History : BottomNavItem(
        route = "history",
        label = R.string.history,
        icon = R.drawable.ic_history
    )

    data object Settings : BottomNavItem(
        route = "settings",
        label = R.string.settings,
        icon = R.drawable.ic_setting
    )
}