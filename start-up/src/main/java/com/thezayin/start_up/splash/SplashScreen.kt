package com.thezayin.start_up.splash

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.thezayin.framework.ads.functions.appOpenAd
import com.thezayin.framework.ads.functions.interstitialAd
import com.thezayin.framework.components.AdLoadingDialog
import com.thezayin.start_up.splash.component.BottomText
import com.thezayin.start_up.splash.component.ImageHeader
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToOnboarding: () -> Unit
) {
    val viewModel: SplashViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    val activity = LocalContext.current as Activity
    val showLoadingAd = remember { mutableStateOf(false) }
    if (showLoadingAd.value) {
        AdLoadingDialog()
    }

    LaunchedEffect(Unit) {
        delay(5000)
        if (viewModel.remoteConfig.adConfigs.switchAdOnSplash) {
            activity.appOpenAd(
                showAd = viewModel.remoteConfig.adConfigs.adOnSplash,
                adUnitId = viewModel.remoteConfig.adUnits.appOpenAd,
                showLoading = { showLoadingAd.value = true },
                hideLoading = { showLoadingAd.value = false },
                callback = { if (state.isFirstTime) navigateToOnboarding() else navigateToHome() },
            )
        } else {
            activity.interstitialAd(
                showAd = viewModel.remoteConfig.adConfigs.adOnSplash,
                adUnitId = viewModel.remoteConfig.adUnits.interstitialAd,
                showLoading = { showLoadingAd.value = true },
                hideLoading = { showLoadingAd.value = false },
                callback = { if (state.isFirstTime) navigateToOnboarding() else navigateToHome() },
            )
        }
    }

    Scaffold(modifier = Modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (state.isLoading) {
                BottomText(modifier = Modifier, text = state.currentSplashText)
            }
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                ImageHeader(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
