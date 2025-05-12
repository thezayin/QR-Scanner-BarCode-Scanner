package com.thezayin.start_up.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.thezayin.framework.components.BannerAd
import com.thezayin.framework.components.ComposableLifecycle
import com.thezayin.framework.components.GoogleNativeSimpleAd
import com.thezayin.start_up.onboarding.OnboardingViewModel
import com.thezayin.start_up.onboarding.model.OnboardingPage
import com.thezayin.values.R
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun OnboardingContent(
    vm: OnboardingViewModel,
    currentPage: Int,
    onboardPages: List<OnboardingPage>,
    onNextClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                scope.coroutineContext.cancelChildren()
                scope.launch {
                    while (this.isActive) {
                        vm.getNativeAd(context)
                        delay(20000L)
                    }
                }
            }

            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = colorResource(R.color.black),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OnBoardDetails(
                    currentPage = onboardPages[currentPage],
                    onNextClicked = onNextClicked
                )
                if (!vm.remoteConfig.adConfigs.switchBottomAdAtOnboarding) {
                    BannerAd(
                        showAd = vm.remoteConfig.adConfigs.bottomAdAtOnboarding,
                        adId = vm.remoteConfig.adUnits.bannerAd
                    )
                } else {
                    GoogleNativeSimpleAd(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        nativeAd = vm.nativeAd.value
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GifDisplay(
                gifResId = onboardPages[currentPage].gifResId,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}