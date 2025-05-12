package com.thezayin.history.presentation.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.thezayin.framework.utils.saveImageToExternalStorage
import com.thezayin.framework.utils.shareImage
import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.presentation.HistoryViewModel
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowQrCodeBottomSheet(
    vm: HistoryViewModel,
    context: Context,
    item: CreateItem,
    onDismiss: () -> Unit
) {
    val adManager = vm.rewardedAdManager
    val activity = context as android.app.Activity
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = state,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.padding(16.sdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter = rememberAsyncImagePainter(item.imageUri)
            Image(
                painter = painter,
                contentDescription = stringResource(id = R.string.qr_code_image),
                modifier = Modifier.size(150.sdp)
            )

            Spacer(modifier = Modifier.height(8.sdp))
            Text(
                text = item.content,
                fontSize = 12.ssp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(8.sdp)
            )
            Spacer(modifier = Modifier.height(16.sdp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    adManager.showAd(
                        activity = activity,
                        showAd = vm.remoteConfig.adConfigs.adOnHistoryDownloadClick,
                        onNext = {
                            saveImageToExternalStorage(context, item.imageUri)
                            Toast.makeText(
                                context,
                                context.getString(R.string.download_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_downlaod),
                            contentDescription = stringResource(id = R.string.download),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                IconButton(onClick = {
                    adManager.showAd(
                        activity = activity,
                        showAd = vm.remoteConfig.adConfigs.adOnHistoryShareClick,
                        onNext = { shareImage(context, item.imageUri) }
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
}
