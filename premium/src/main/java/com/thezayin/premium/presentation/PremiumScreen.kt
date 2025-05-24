package com.thezayin.premium.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.framework.utils.openPrivacy
import com.thezayin.framework.utils.openTerms
import com.thezayin.premium.presentation.components.PlanOption
import com.thezayin.premium.presentation.event.PremiumEvent.CheckSubscriptionStatus
import com.thezayin.premium.presentation.event.PremiumEvent.LoadPremiumStatus
import com.thezayin.premium.presentation.event.PremiumEvent.LoadSubscriptionPrices
import com.thezayin.premium.presentation.event.PremiumEvent.SubscribeToWeekly
import com.thezayin.premium.presentation.event.PremiumEvent.SubscribeToYearly
import com.thezayin.premium.presentation.state.PlanType
import org.koin.compose.koinInject

@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf(PlanType.Weekly) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(LoadSubscriptionPrices)
        viewModel.onEvent(CheckSubscriptionStatus)
        viewModel.onEvent(LoadPremiumStatus)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(com.thezayin.values.R.drawable.bg_premium),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Unlimited Access\nTo All Features",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                listOf(
                    "Scan QR Codes", "Scan Barcodes", "No Ads and No Limits"
                ).forEach { feature ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Check, contentDescription = null, tint = Color(0xFFB388FF)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(feature, color = Color.White, fontSize = 16.sp)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PlanOption(
                    title = "Weekly Plan",
                    price = state.weeklyPrice?.let { "Rs $it/week" } ?: "--",
                    selected = selectedPlan == PlanType.Weekly) { selectedPlan = PlanType.Weekly }
                Spacer(Modifier.height(12.dp))
                PlanOption(
                    title = "Annual Plan",
                    price = state.yearlyPrice?.let { "Rs $it/year" } ?: "--",
                    selected = selectedPlan == PlanType.Yearly) { selectedPlan = PlanType.Yearly }
                Spacer(Modifier.height(24.dp))

                if (state.isLoading) {
                    CircularProgressIndicator(color = Color(0xFFB388FF))
                } else {
                    Button(
                        onClick = {
                            if (selectedPlan == PlanType.Weekly) viewModel.onEvent(SubscribeToWeekly)
                            else viewModel.onEvent(SubscribeToYearly)
                        },
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB388FF))
                    ) {
                        Text("Continue", color = Color.White, fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Privacy Policy",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { context.openPrivacy() })
                    Text(
                        "Cancel Anytime", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp
                    )
                    Text(
                        "Terms & Conditions",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { context.openTerms() })
                }
            }
        }

        state.errorMessage?.let { msg ->
            Snackbar(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(msg, color = Color.White)
            }
        }
    }
}