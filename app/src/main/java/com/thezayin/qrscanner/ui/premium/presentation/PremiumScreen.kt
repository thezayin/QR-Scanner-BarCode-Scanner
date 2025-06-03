@file:Suppress("KotlinConstantConditions")

package com.thezayin.qrscanner.ui.premium.presentation

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.thezayin.qrscanner.ui.premium.presentation.action.PremiumActions
import com.thezayin.qrscanner.ui.premium.presentation.components.BottomSection
import com.thezayin.qrscanner.ui.premium.presentation.components.PlanOption
import com.thezayin.qrscanner.ui.premium.presentation.components.SubscriptionButton
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import timber.log.Timber


@Composable
fun PremiumScreen(
    navigateBack: () -> Unit,
    viewModel: PremiumViewModel = koinInject()
) {
    val state by viewModel.premiumState.collectAsState()
    val context = LocalContext.current
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage.isNotEmpty()) {
            snackbarMessage = state.errorMessage
            showSnackbar = true
        }
    }
    LaunchedEffect(state.isPremium) {
        if (state.isPremium) {
            Timber.d("Premium status is now TRUE. User is premium.")
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(R.drawable.bg_premium),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.sdp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier,
                        onClick = {
                            navigateBack()
                        }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            tint = Color.Black,
                            modifier = Modifier.size(15.sdp),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.sdp))
                Text(
                    "Unlimited Access",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.ssp
                )
                Spacer(modifier = Modifier.height(5.sdp))
                Text(
                    "To All Features",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.ssp
                )
                Spacer(modifier = Modifier.height(30.sdp))
                listOf(
                    "Scan QR Codes", "Scan Barcodes", "No Ads and No Limits"
                ).forEach { feature ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Check, contentDescription = null, tint = Color(0xFFB388FF)
                        )
                        Spacer(Modifier.width(8.sdp))
                        Text(feature, color = Color.White, fontSize = 14.ssp)
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.sdp)
            ) {
                if (state.isLoading && state.selectedPlan != null) {
                    CircularProgressIndicator(color = Color(0xFFB388FF))
                    Spacer(modifier = Modifier.height(10.sdp))
                    Text("Processing...", color = Color.White)

                } else if (state.subscriptions.isNotEmpty()) {
                    if (state.isLoading && state.subscriptions.isEmpty()) {
                        CircularProgressIndicator(color = Color(0xFFB388FF))
                    } else if (state.subscriptions.isEmpty() && !state.isLoading && state.errorMessage.isNotEmpty()) {
                        Text(
                            text = "Could not load offers. Please check your connection and try again.",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.sdp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.sdp)
                        ) {
                            items(state.subscriptions) { subscription ->
                                PlanOption(
                                    subscription = subscription,
                                    selected = subscription == state.selectedPlan
                                ) {
                                    viewModel.onAction(
                                        PremiumActions.SubscriptionSelected(
                                            subscription
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(30.sdp))
                    SubscriptionButton(
                        enabled = state.selectedPlan != null && !state.isLoading, onClick = {
                            if (context is Activity) {
                                viewModel.purchaseSelectedPlan(context)
                            } else {
                                snackbarMessage = "Error: Cannot initiate purchase."
                                showSnackbar = true
                            }
                        })
                }
                Spacer(modifier = Modifier.height(10.sdp))
                BottomSection(context)
            }
        }

        if (showSnackbar) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.sdp), action = {
                    Button(onClick = { showSnackbar = false }) {
                        Text("Dismiss")
                    }
                }) {
                Text(snackbarMessage, color = Color.White)
            }
            LaunchedEffect(snackbarMessage) {
                delay(4000)
                showSnackbar = false
            }
        }
    }
}