package com.thezayin.start_up.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.thezayin.start_up.onboarding.model.OnboardingPage
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun OnBoardDetails(
    modifier: Modifier = Modifier, currentPage: OnboardingPage, onNextClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(12.sdp)
    ) {
        Text(
            text = currentPage.title,
            fontSize = 22.ssp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.sdp))
        Text(
            text = currentPage.subtitle,
            fontSize = 12.ssp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = colorResource(R.color.reset_bg_normal_color)
        )
        OnBoardNavButton(
            modifier = Modifier.padding(top = 10.sdp, bottom = 10.sdp),
        ) {
            onNextClicked()
        }
    }
}