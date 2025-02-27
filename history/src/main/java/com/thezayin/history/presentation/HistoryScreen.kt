package com.thezayin.history.presentation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.thezayin.framework.ads.functions.interstitialAd
import com.thezayin.framework.components.AdLoadingDialog
import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.presentation.components.HistoryScreenContent
import com.thezayin.history.presentation.components.ShowQrCodeBottomSheet
import com.thezayin.history.presentation.event.HistoryEvent
import org.koin.compose.koinInject

@Composable
fun HistoryScreen(
    onNavigateUp: () -> Unit,
    navigateToScanItem: () -> Unit,
    viewModel: HistoryViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    var selectedItem: CreateItem? by remember { mutableStateOf(null) }
    val activity = LocalContext.current as Activity
    val showLoadingAd = remember { mutableStateOf(false) }

    if (showLoadingAd.value) {
        AdLoadingDialog()
    }

    selectedItem?.let { item ->
        ShowQrCodeBottomSheet(
            context = LocalContext.current,
            showLoadingAd = showLoadingAd,
            vm = viewModel,
            item = item,
            onDismiss = { selectedItem = null }
        )
    }

    HistoryScreenContent(
        state = state,
        onNavigateUp = onNavigateUp,
        onTabSelected = { tab -> viewModel.onEvent(HistoryEvent.SelectTab(tab)) },
        onScanItemClick = { scanItem ->
            activity.interstitialAd(
                showAd = viewModel.remoteConfig.adConfigs.adOnHistoryCardClick,
                adUnitId = viewModel.remoteConfig.adUnits.interstitialAd,
                showLoading = { showLoadingAd.value = true },
                hideLoading = { showLoadingAd.value = false },
                callback = {
                    viewModel.onEvent(HistoryEvent.OpenScanItem(scanItem))
                    navigateToScanItem()
                }
            )
        },
        onToggleFavorite = { scanItem ->
            viewModel.onEvent(HistoryEvent.ToggleScanFavorite(scanItem))
        },
        onScanDelete = { scanItem ->
            viewModel.onEvent(HistoryEvent.DeleteScanItem(scanItem))
        },
        onCreateDelete = { createItem ->
            viewModel.onEvent(HistoryEvent.DeleteCreateItem(createItem))
        },
        onItemClicked = { createItem ->
            selectedItem = createItem
        })
}