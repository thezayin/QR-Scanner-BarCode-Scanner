package com.thezayin.start_up.splash

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thezayin.start_up.splash.component.BottomText
import com.thezayin.start_up.splash.component.ImageHeader
import com.thezayin.values.R
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToOnboarding: () -> Unit
) {
    val vm: SplashViewModel = koinInject()
    val state by vm.state.collectAsState()
    val activity = LocalActivity.current as Activity
    val appOpenAdManager = vm.appOpenAdManager
    val interstitialAdManager = vm.interstitialAdManager

    LaunchedEffect(Unit) {
        vm.initManagers(activity)
        delay(5000)
        if (vm.remoteConfig.adConfigs.switchAdOnSplash) {
            appOpenAdManager.showAd(
                activity = activity,
                showAd = vm.remoteConfig.adConfigs.adOnSplash,
                onNext = { if (state.isFirstTime) navigateToOnboarding() else navigateToHome() },
            )
        } else {
            interstitialAdManager.showAd(
                activity = activity,
                showAd = vm.remoteConfig.adConfigs.adOnSplash,
                onNext = { if (state.isFirstTime) navigateToOnboarding() else navigateToHome() },
            )
        }
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (state.isLoading) {
                BottomText(modifier = Modifier, text = state.currentSplashText)
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoading) {
                ImageHeader(modifier = Modifier)
                Spacer(modifier = Modifier.padding(16.dp))
                Text(
                    text = activity.getString(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}