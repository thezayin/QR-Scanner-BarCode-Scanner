package com.thezayin.start_up.onboarding

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.thezayin.start_up.onboarding.actions.OnboardingActions
import com.thezayin.start_up.onboarding.components.OnboardingContent
import org.koin.compose.koinInject

@Composable
fun OnboardingScreen(
    navigateToHome: () -> Unit,
    vm: OnboardingViewModel = koinInject()
) {
    val state = vm.state.collectAsState()
    val activity = LocalContext.current as Activity

    val appOpenAdManager = vm.appOpenAdManager
    val interstitialAdManager = vm.interstitialAdManager

    LaunchedEffect(Unit) {
        vm.initManagers(activity)
    }

    if (state.value.isOnboardingCompleted) {
        navigateToHome()
        return
    }

    OnboardingContent(
        vm = vm,
        onboardPages = state.value.pages,
        currentPage = state.value.currentPage,
        onNextClicked = {
            if (state.value.currentPage < state.value.pages.size - 1) {
                vm.onAction(OnboardingActions.NextPage)
            } else {
                if (vm.remoteConfig.adConfigs.switchAdOnSplash) {
                    appOpenAdManager.showAd(
                        activity = activity,
                        showAd = vm.remoteConfig.adConfigs.adOnSplash,
                        onNext = { vm.onAction(OnboardingActions.CompleteOnboarding) },
                    )
                } else {
                    interstitialAdManager.showAd(
                        activity = activity,
                        showAd = vm.remoteConfig.adConfigs.adOnSplash,
                        onNext = { vm.onAction(OnboardingActions.CompleteOnboarding) },
                    )
                }
            }
        }
    )
}