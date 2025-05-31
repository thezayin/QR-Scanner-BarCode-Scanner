@file:Suppress("DEPRECATION")

package com.thezayin.qrscanner.navigation

// Removed ExperimentalAnimationApi import, as we are now using NavHost directly.
// import androidx.compose.animation.ExperimentalAnimationApi // REMOVE THIS
// Also remove tween, slideInHorizontally, slideOutHorizontally imports.

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.qrscanner.ui.language.ui.LanguageScreen
import com.thezayin.start_up.onboarding.OnboardingScreen
import com.thezayin.start_up.splash.SplashScreen

// Removed @OptIn(ExperimentalAnimationApi::class) annotation since AnimatedNavHost is no longer used.
@Composable
fun RootNavGraph(
    primaryColor: Color,
    remoteConfig: RemoteConfig,
    nativeAd: NativeAd?,
    preferencesManager: PreferencesManager // PreferencesManager is needed here for logic
) {
    val navController = rememberNavController()

    // Using NavHost directly without explicit animations for efficiency and no jank from transitions.
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(
                navigateToHome = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true } // Clear splash from stack
                    }
                },
                navigateToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true } // Clear splash from stack
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                navigateToHome = {
                    // Logic: After Onboarding, decide between Language and Main based on Preferences
                    val hasLanguageBeenExplicitlySet = preferencesManager.getSavedLanguage() != null

                    if (!hasLanguageBeenExplicitlySet) {
                        // If language not set, go to language selection
                        navController.navigate("language") {
                            popUpTo("onboarding") { inclusive = true } // Clear onboarding
                        }
                    } else {
                        // Language already set, go directly to main content
                        navController.navigate("main") {
                            popUpTo("onboarding") { inclusive = true } // Clear onboarding
                        }
                    }
                }
            )
        }

        composable("language") {
            LanguageScreen(
                onNavigateBack = {
                    // If the user presses back from Language selection (which was navigated from onboarding),
                    // simply pop back. This implies exiting or returning to previous app state depending on stack.
                    // If language is a mandatory step after onboarding, backing out might imply a fresh start or app exit.
                    navController.popBackStack()
                },
                onCurrentLanguageConfirmed = {
                    // After language is confirmed (newly selected or re-confirmed current), navigate to main.
                    navController.navigate("main") {
                        popUpTo("language") { inclusive = true } // Clear language from stack
                        // Removed popUpTo("onboarding") here.
                        // If onboarding was cleared before entering language screen, this was redundant.
                        // If onboarding *wasn't* cleared (unlikely given your current nav setup),
                        // decide if you want to keep it or explicitly clear it (which can be a heavy operation).
                        // It's generally better to clear previous screens when moving to a 'main' hub.
                        // You could add: popUpTo("onboarding") { inclusive = true }
                        // if onboarding still sometimes remains on stack, but generally previous popUpTo is enough.
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