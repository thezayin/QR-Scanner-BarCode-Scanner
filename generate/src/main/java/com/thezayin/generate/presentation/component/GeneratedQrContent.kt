package com.thezayin.generate.presentation.component

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.generate.presentation.GenerateViewModel
import com.thezayin.generate.presentation.event.GenerateEvent
import com.thezayin.generate.presentation.state.GenerateState
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun GeneratedQrContent(
    viewModel: GenerateViewModel,
    remoteConfig: RemoteConfig,
    state: GenerateState,
    onEvent: (GenerateEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val adManager = viewModel.adManager
    val rewardedAdManager = viewModel.rewardedAdManager
    val activity = LocalActivity.current as Activity

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.sdp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.generatedQrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(id = R.string.generated_qr_code),
                modifier = Modifier.size(200.sdp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                rewardedAdManager.showAd(
                    activity = activity,
                    showAd = remoteConfig.adConfigs.adOnHistoryDownloadClick,
                    onNext = {
                        onEvent(GenerateEvent.DownloadQrCode)
                    }
                )
            }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.ic_downlaod),
                        contentDescription = stringResource(id = R.string.download),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            IconButton(onClick = {
                adManager.showAd(
                    activity = activity,
                    showAd = remoteConfig.adConfigs.adOnHistoryDownloadClick,
                    onNext = {
                        onEvent(GenerateEvent.ShareQrCode)
                    }
                )
            }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(id = R.string.share),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}