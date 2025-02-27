@file:Suppress("DEPRECATION")

package com.thezayin.qrscanner.navigation

import android.app.Activity
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.thezayin.framework.ads.functions.interstitialAd
import com.thezayin.framework.ads.loader.GoogleBannerAdLoader
import com.thezayin.framework.components.AdLoadingDialog
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.generate.presentation.GenerateScreen
import com.thezayin.history.presentation.FavoritesScreen
import com.thezayin.history.presentation.HistoryScreen
import com.thezayin.scanner.presentation.result.ResultScreen
import com.thezayin.scanner.presentation.scanner.ScannerScreen
import com.thezayin.start_up.languages.LanguageScreen
import com.thezayin.start_up.setting.SettingsScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNav(
    primaryColor: Color,
    remoteConfig: RemoteConfig
) {
    val navController = rememberNavController()
    val bottomNavRoutes = listOf(
        BottomNavItem.Scan.route,
        BottomNavItem.Create.route,
        BottomNavItem.History.route,
        BottomNavItem.Settings.route
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomNavItems = listOf(
        BottomNavItem.Scan, BottomNavItem.Create, BottomNavItem.History, BottomNavItem.Settings
    )
    val showLoadingAd = remember { mutableStateOf(false) }
    val showExitDialog = remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (currentRoute == BottomNavItem.Scan.route) {
            showExitDialog.value = true
        } else {
            navController.popBackStack()
        }
    }

    val activity = LocalContext.current as Activity
    if (showLoadingAd.value) {
        AdLoadingDialog()
    }
    val bottomBarHeight = if (currentRoute in bottomNavRoutes) {
        if (remoteConfig.adConfigs.adOnBottomHome) 56.dp + 50.dp else 56.dp
    } else {
        0.dp
    }



    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedNavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomBarHeight),
            navController = navController,
            startDestination = BottomNavItem.Scan.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = { fraction ->
                            val interpolator = android.view.animation.OvershootInterpolator(2f)
                            interpolator.getInterpolation(fraction)
                        }
                    )
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = { fraction ->
                            val interpolator = android.view.animation.AnticipateInterpolator(2f)
                            interpolator.getInterpolation(fraction)
                        }
                    )
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = { fraction ->
                            val interpolator = android.view.animation.AnticipateInterpolator(2f)
                            interpolator.getInterpolation(fraction)
                        }
                    )
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = { fraction ->
                            val interpolator = android.view.animation.OvershootInterpolator(2f)
                            interpolator.getInterpolation(fraction)
                        }
                    )
                )
            }
        ) {
            composable(BottomNavItem.Scan.route) {
                ScannerScreen(onScanSuccess = {
                    navController.navigate("result")
                })
            }
            composable(BottomNavItem.Create.route) {
                GenerateScreen(
                    onNavigateBack = { navController.navigate(BottomNavItem.Scan.route) }
                )
            }
            composable(BottomNavItem.History.route) {
                HistoryScreen(
                    onNavigateUp = { navController.navigate(BottomNavItem.Scan.route) },
                    navigateToScanItem = {
                        navController.navigate("result")
                    }
                )
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    navigateToLanguage = { navController.navigate("languages") },
                    onNavigateBack = { navController.navigate(BottomNavItem.Scan.route) },
                    navigateToFavourite = { navController.navigate("favourite") }
                )
            }
            composable("favourite") {
                FavoritesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    navigateToScanItem = { navController.navigate("result") }
                )
            }
            composable("result") {
                ResultScreen(onNavigateUp = { navController.popBackStack() })
            }
            composable("languages") {
                LanguageScreen(
                    onLanguageSelection = {
                        activity.interstitialAd(
                            showAd = remoteConfig.adConfigs.adOnSplash,
                            adUnitId = remoteConfig.adUnits.interstitialAd,
                            showLoading = { showLoadingAd.value = true },
                            hideLoading = { showLoadingAd.value = false },
                            callback = { navController.navigateUp() }
                        )
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }

        if (currentRoute in bottomNavRoutes) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
            ) {
                BottomNavigationBar(
                    navController = navController,
                    items = bottomNavItems,
                    primaryColor = primaryColor,
                    remoteConfig = remoteConfig
                )
                if (remoteConfig.adConfigs.adOnBottomHome) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        factory = { context ->
                            val adView = GoogleBannerAdLoader.getBannerAd(
                                context,
                                remoteConfig.adUnits.bannerAd
                            )
                            val frameLayout = FrameLayout(context).apply {
                                layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                                )
                            }
                            frameLayout.addView(adView)
                            adView.visibility = android.view.View.VISIBLE
                            frameLayout
                        }
                    )
                }
            }
        }
    }
    if (showExitDialog.value) {
        AlertDialog(
            onDismissRequest = { showExitDialog.value = false },
            title = { Text(text = "Exit App") },
            text = { Text(text = "Are you sure you want to exit?") },
            confirmButton = {
                Button(onClick = {
                    showExitDialog.value = false
                    activity.interstitialAd(
                        showAd = remoteConfig.adConfigs.adOnSplash,
                        adUnitId = remoteConfig.adUnits.interstitialAd,
                        showLoading = { showLoadingAd.value = true },
                        hideLoading = { showLoadingAd.value = false },
                        callback = { activity.finish() }
                    )
                }) {
                    Text(text = "Exit")
                }
            },
            dismissButton = {
                Button(onClick = { showExitDialog.value = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}