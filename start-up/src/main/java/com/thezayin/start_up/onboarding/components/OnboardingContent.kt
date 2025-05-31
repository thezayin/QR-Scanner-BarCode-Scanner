package com.thezayin.start_up.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.thezayin.start_up.onboarding.OnboardingViewModel
import com.thezayin.start_up.onboarding.model.OnboardingPage
import com.thezayin.values.R

@Composable
fun OnboardingContent(
    vm: OnboardingViewModel,
    currentPage: Int,
    onboardPages: List<OnboardingPage>,
    onNextClicked: () -> Unit
) {
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