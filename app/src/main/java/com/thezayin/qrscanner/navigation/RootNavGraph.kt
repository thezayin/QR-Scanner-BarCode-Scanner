@file:Suppress("DEPRECATION")

package com.thezayin.qrscanner.navigation

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.qrscanner.activity.MainActivity
import com.thezayin.qrscanner.ui.language.ui.LanguageScreen
import com.thezayin.qrscanner.ui.language.ui.LanguageScreenResult
import com.thezayin.qrscanner.ui.onboarding.OnboardingScreen
import com.thezayin.start_up.splash.SplashScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavGraph(
    primaryColor: Color,
    remoteConfig: RemoteConfig,
    nativeAd: NativeAd?,
    preferencesManager: PreferencesManager
) {
    val navController = rememberNavController()
    val activity = LocalActivity.current as? MainActivity
    val context = LocalContext.current

    val isFirstTimeUser by preferencesManager.isFirstTime.collectAsState()

    val initialRootGraphRoute = remember(isFirstTimeUser) {
        if (isFirstTimeUser) {
            "splash"
        } else {
            "main"
        }
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = initialRootGraphRoute,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(500)
            )
        }) {
        composable("splash") {
            SplashScreen(navigateToHome = {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }, navigateToOnboarding = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("onboarding") {
            OnboardingScreen(
                onNavigateToLanguage = {
                    navController.navigate("language") {
                    }
                })
        }

        composable("language") {
            val currentIsFirstTimeUser =
                preferencesManager.isFirstTime.value

            LanguageScreen(
                isFromOnboarding = currentIsFirstTimeUser,
                onNavigateFinished = { result ->
                    when (result) {
                        is LanguageScreenResult.RecreateApp -> {
                            val intent = Intent(context, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                            context.startActivity(intent)
                            activity?.finish()
                            if (currentIsFirstTimeUser) {
                                preferencesManager.setOnboardingCompleted()
                            }
                        }

                        is LanguageScreenResult.FinalizeOnboarding -> {
                            navController.navigate("main") {
                                popUpTo("onboarding") {
                                    inclusive = true
                                }
                                preferencesManager.setOnboardingCompleted()
                            }
                        }

                        is LanguageScreenResult.GoBackToCaller -> {
                            navController.popBackStack()
                        }
                    }
                })
        }

        composable("main") {
            MainNav(
                preferencesManager = preferencesManager,
                primaryColor = primaryColor,
                remoteConfig = remoteConfig,
                nativeAd = nativeAd,
                navigateToLanguageScreenRoot = {
                    navController.navigate("language") {
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}