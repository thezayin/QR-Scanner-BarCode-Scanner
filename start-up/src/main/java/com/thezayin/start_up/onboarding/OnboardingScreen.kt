package com.thezayin.start_up.onboarding

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.thezayin.framework.ads.functions.appOpenAd
import com.thezayin.framework.ads.functions.interstitialAd
import com.thezayin.framework.components.AdLoadingDialog
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
    val showLoadingAd = remember { mutableStateOf(false) }
    if (showLoadingAd.value) {
        AdLoadingDialog()
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
                    activity.appOpenAd(
                        showAd = vm.remoteConfig.adConfigs.adOnSplash,
                        adUnitId = vm.remoteConfig.adUnits.appOpenAd,
                        showLoading = { showLoadingAd.value = true },
                        hideLoading = { showLoadingAd.value = false },
                        callback = { vm.onAction(OnboardingActions.CompleteOnboarding) },
                    )
                } else {
                    activity.interstitialAd(
                        showAd = vm.remoteConfig.adConfigs.adOnSplash,
                        adUnitId = vm.remoteConfig.adUnits.interstitialAd,
                        showLoading = { showLoadingAd.value = true },
                        hideLoading = { showLoadingAd.value = false },
                        callback = { vm.onAction(OnboardingActions.CompleteOnboarding) },
                    )
                }
            }
        }
    )
}