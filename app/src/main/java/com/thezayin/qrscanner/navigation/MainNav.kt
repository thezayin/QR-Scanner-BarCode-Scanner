@file:Suppress("DEPRECATION")

package com.thezayin.qrscanner.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.components.BannerAd
import com.thezayin.framework.components.GoogleNativeSimpleAd
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.generate.presentation.GenerateScreen
import com.thezayin.history.presentation.FavoritesScreen
import com.thezayin.history.presentation.HistoryScreen
import com.thezayin.qrscanner.activity.MainActivity
import com.thezayin.qrscanner.ui.premium.presentation.PremiumScreen
import com.thezayin.scanner.presentation.result.ResultScreen
import com.thezayin.scanner.presentation.scanner.ScannerScreen
import com.thezayin.start_up.setting.SettingsScreen
import com.thezayin.values.R
import org.koin.compose.koinInject

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNav(
    primaryColor: Color,
    remoteConfig: RemoteConfig,
    nativeAd: NativeAd?,
    preferencesManager: PreferencesManager,
    navigateToLanguageScreenRoot: () -> Unit
) {
    val activity = LocalActivity.current as MainActivity
    val adManager = koinInject<InterstitialAdManager>()

    LaunchedEffect(Unit) {
        adManager.loadAd(activity)
    }

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
    val showExitDialog = remember { mutableStateOf(false) }
    BackHandler(enabled = true) {
        if (currentRoute == BottomNavItem.Scan.route) {
            showExitDialog.value = true
        } else {
            navController.popBackStack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .navigationBarsPadding()
                ) {
                    BottomNavigationBar(
                        navController = navController,
                        items = bottomNavItems,
                        primaryColor = primaryColor,
                        remoteConfig = remoteConfig
                    )
                    if (remoteConfig.adConfigs.adOnBottomHome) {
                        BannerAd(
                            preferencesManager = preferencesManager,
                            showAd = remoteConfig.adConfigs.adOnBottomHome,
                            adId = remoteConfig.adUnits.bannerAd
                        )
                    }
                }
            }
        }) { padding ->
        AnimatedNavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            navController = navController,
            startDestination = BottomNavItem.Scan.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(
                        durationMillis = 500, easing = { fraction ->
                            val interpolator = android.view.animation.OvershootInterpolator(2f)
                            interpolator.getInterpolation(fraction)
                        })
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(
                        durationMillis = 500, easing = { fraction ->
                            val interpolator = android.view.animation.AnticipateInterpolator(2f)
                            interpolator.getInterpolation(fraction)
                        })
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(
                        durationMillis = 500, easing = { fraction ->
                            val interpolator = android.view.animation.AnticipateInterpolator(2f)
                            interpolator.getInterpolation(fraction)
                        })
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(
                        durationMillis = 500, easing = { fraction ->
                            val interpolator = android.view.animation.OvershootInterpolator(2f)
                            interpolator.getInterpolation(fraction)
                        })
                )
            }) {
            composable(BottomNavItem.Scan.route) {
                ScannerScreen(onSuccessfulScanNavigation = {
                    navController.navigate("result")
                })
            }
            composable(BottomNavItem.Create.route) {
                GenerateScreen(
                    onNavigateBack = { navController.navigate(BottomNavItem.Scan.route) },
                    navigateToPremium = { navController.navigate("premium") })
            }
            composable(BottomNavItem.History.route) {
                HistoryScreen(
                    onNavigateUp = { navController.navigate(BottomNavItem.Scan.route) },
                    navigateToScanItem = { navController.navigate("result") },
                    navigateToPremium = { navController.navigate("premium") })
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    navigateToLanguage = {
                        navigateToLanguageScreenRoot()
                    },
                    navigateToPremium = { navController.navigate(("premium")) },
                    onNavigateBack = { navController.navigate(BottomNavItem.Scan.route) },
                    navigateToFavourite = { navController.navigate("favourite") })
            }
            composable("favourite") {
                FavoritesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    navigateToScanItem = { navController.navigate("result") },
                    navigateToPremium = { navController.navigate("premium") })
            }
            composable("result") {
                ResultScreen(
                    onNavigateUp = { navController.popBackStack() },
                    navigateToPremium = { navController.navigate("premium") })
            }
            composable("premium") {
                PremiumScreen(navigateBack = { navController.navigateUp() })
            }
        }
    }
    if (showExitDialog.value) {
        Dialog(onDismissRequest = { showExitDialog.value = false }) {
            Card(modifier = Modifier) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Exit App",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Are you sure you want to exit?",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            modifier = Modifier.width(100.dp), colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.allports),
                                contentColor = colorResource(R.color.white)
                            ), onClick = {
                                showExitDialog.value = false
                                adManager.showAd(
                                    activity = activity,
                                    showAd = remoteConfig.adConfigs.adOnSplash,
                                    onNext = { activity.finish() })
                            }) {
                            Text(text = "Exit")
                        }
                        Button(
                            modifier = Modifier.width(100.dp), colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.allports),
                                contentColor = colorResource(R.color.white)
                            ), onClick = { showExitDialog.value = false }) {
                            Text(text = "Cancel")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (nativeAd != null) {
                        GoogleNativeSimpleAd(
                            modifier = Modifier.fillMaxWidth(), nativeAd = nativeAd
                        )
                    }
                }
            }
        }
    }
}