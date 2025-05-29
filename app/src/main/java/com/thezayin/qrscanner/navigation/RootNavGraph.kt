@file:Suppress("DEPRECATION")

package com.thezayin.qrscanner.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.qrscanner.ui.language.ui.LanguageScreen
import com.thezayin.qrscanner.ui.language.ui.LanguageViewModel
import com.thezayin.start_up.onboarding.OnboardingScreen
import com.thezayin.start_up.splash.SplashScreen
import org.koin.compose.koinInject
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavGraph(
    primaryColor: Color,
    remoteConfig: RemoteConfig,
    nativeAd: NativeAd?,
    preferencesManager: PreferencesManager
) {
    val navController = rememberNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(500)
            )
        }
    ) {
        composable("splash") {
            SplashScreen(
                navigateToHome = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                navigateToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                navigateToHome = {
                    navController.navigate("language") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("language") {
            val languageViewModel: LanguageViewModel = koinInject()
            LanguageScreen(
                viewModel = languageViewModel,
                onNavigateBack = {
                    // If user presses back from language screen during initial setup, go back to onboarding
                    navController.navigate("onboarding") {
                        popUpTo("language") { inclusive = true } // Avoids multiple language screens on backstack
                        // You might want to consider singleTop for "onboarding" or launchSingleTop = true
                        // if there's a chance of creating multiple onboarding instances.
                        // For now, this ensures going back from language during setup returns to one onboarding screen.
                    }
                },
                onCurrentLanguageConfirmed = {
                    // User tapped the already selected language (e.g., default English) during initial setup.
                    // Proceed to the main application. No app restart needed here.
                    Timber.tag("jajaRootNavGraph")
                        .d("Language confirmed during initial setup. Navigating to main.")
                    navController.navigate("main") {
                        popUpTo("language") { inclusive = true } // Remove language screen from back stack
                        popUpTo("onboarding") { inclusive = true } // Also remove onboarding
                    }
                }
            )
        }
        composable("main") {
            MainNav(
                preferencesManager = preferencesManager,
                primaryColor = primaryColor,
                remoteConfig = remoteConfig,
                nativeAd = nativeAd
            )
        }
    }
}
