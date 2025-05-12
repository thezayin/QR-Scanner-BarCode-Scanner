package com.thezayin.qrscanner.navigation

import android.app.Activity
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.remote.RemoteConfig
import org.koin.compose.koinInject

@Composable
fun BottomNavigationBar(
    remoteConfig: RemoteConfig,
    navController: NavHostController,
    items: List<BottomNavItem>,
    primaryColor: Color
) {
    val activity = LocalContext.current as Activity
    val adManager = koinInject<InterstitialAdManager>()

    LaunchedEffect(Unit) {
        adManager.loadAd(activity)
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = primaryColor.copy(alpha = 0.2f)
                ),
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        tint = if (currentRoute == item.route) primaryColor else MaterialTheme.colorScheme.onSurface,
                        contentDescription = stringResource(item.label)
                    )
                },
                label = { Text(stringResource(item.label)) },
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route != currentRoute) {
                        if (item.route == "create" || item.route == "history") {
                            adManager.showAd(
                                activity = activity,
                                showAd = remoteConfig.adConfigs.adOnBottomMenu,
                                onNext = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            )
                        } else {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    }
}