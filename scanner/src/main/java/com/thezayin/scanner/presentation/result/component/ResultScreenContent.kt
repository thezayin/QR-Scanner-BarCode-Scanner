package com.thezayin.scanner.presentation.result.component

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.thezayin.framework.components.ComposableLifecycle
import com.thezayin.framework.components.GoogleNativeSimpleAd
import com.thezayin.framework.utils.billing.isPremium
import com.thezayin.scanner.presentation.result.ResultScreenViewModel
import com.thezayin.scanner.presentation.result.state.ResultScreenState
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun ResultScreenContent(
    state: ResultScreenState,
    viewModel: ResultScreenViewModel,
    onNavigateUp: () -> Unit,
    navigateToPremium: () -> Unit
) {

    val activity = LocalActivity.current as Activity
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val adManager = viewModel.adManager

    LaunchedEffect(Unit) {
        viewModel.initManager(activity)
    }

    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                scope.coroutineContext.cancelChildren()
                scope.launch {
                    while (this.isActive) {
                        viewModel.getNativeAd(context)
                        delay(20000L)
                    }
                }
            }

            else -> Unit
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ResultTopBar(
                isPremium = viewModel.preferencesManager.isPremiumFlow.value,
                navigateToPremium = navigateToPremium,
                onNavigateUp = {
                    adManager.showAd(
                        activity = activity,
                        showAd = viewModel.remoteConfig.adConfigs.adOnScanResultBackClick,
                        onNext = {
                            onNavigateUp()
                        },
                    )
                },
            )
        },
        bottomBar = {
            GoogleNativeSimpleAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(150.dp),
                nativeAd = viewModel.nativeAd.value
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.sdp))
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(state.scanItems) { item ->
                    when (item.type) {
                        "WIFI" -> WifiResultCard(item = item, vm = viewModel)
                        else -> {
                            if (item.productFound == true) {
                                ProductFoundCard(
                                    item = item,
                                    scannedResult = item.result,
                                    sessionImageUri = item.imageUrl ?: "",
                                    vm = viewModel
                                )
                            } else {
                                ScanResultCard(
                                    item = item,
                                    vm = viewModel,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}