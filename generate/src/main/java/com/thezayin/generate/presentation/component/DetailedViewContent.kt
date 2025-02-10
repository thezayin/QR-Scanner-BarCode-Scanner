package com.thezayin.generate.presentation.component

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thezayin.framework.ads.functions.interstitialAd
import com.thezayin.generate.domain.model.QrType
import com.thezayin.generate.presentation.GenerateViewModel
import com.thezayin.generate.presentation.event.GenerateEvent
import com.thezayin.generate.presentation.state.GenerateState
import ir.kaaveh.sdpcompose.sdp

@Composable
fun DetailedViewContent(
    state: GenerateState,
    allTypes: List<QrType>,
    onEvent: (GenerateEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GenerateViewModel,
    showLoadingAd: MutableState<Boolean>,
) {
    val activity = LocalContext.current as Activity
    val lazyListState = rememberLazyListState()
    LaunchedEffect(state.selectedType) {
        val index = allTypes.indexOf(state.selectedType)
        if (index >= 0) {
            lazyListState.animateScrollToItem(index)
        }
    }

    Column(modifier = modifier) {
        LazyRow(
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(8.sdp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(allTypes) { type ->
                QrTypeItem(
                    type = type,
                    isSelected = (type == state.selectedType),
                    onSelect = { selected ->
                        activity.interstitialAd(
                            showAd = viewModel.remoteConfig.adConfigs.adOnCreateOption,
                            adUnitId = viewModel.remoteConfig.adUnits.interstitialAd,
                            showLoading = { showLoadingAd.value = true },
                            hideLoading = { showLoadingAd.value = false },
                            callback = {
                                onEvent(GenerateEvent.SelectType(selected))
                            },
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.sdp)
                        .then(Modifier.width(100.sdp))
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        QrTypeViews(
            viewModel = viewModel,
            showLoadingAd = showLoadingAd,
            state = state,
            onEvent = onEvent
        )
    }
}