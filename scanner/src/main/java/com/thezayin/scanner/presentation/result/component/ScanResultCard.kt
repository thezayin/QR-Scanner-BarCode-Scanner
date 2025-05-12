package com.thezayin.scanner.presentation.result.component

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import coil.compose.AsyncImage
import com.thezayin.framework.utils.formatTimestamp
import com.thezayin.framework.utils.getDisplayText
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.presentation.result.ResultScreenViewModel
import com.thezayin.scanner.presentation.result.event.ResultScreenEvent
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun ScanResultCard(
    item: ResultScreenItem,
    vm: ResultScreenViewModel
) {
    val context = LocalContext.current
    val displayText = getDisplayText(item.result)
    val activity = context as Activity
    val adManager = vm.adManager

    Card(
        modifier = Modifier
            .padding(horizontal = 10.sdp)
            .padding(top = 20.sdp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(8.sdp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.sdp)
                        .padding(4.sdp)
                )
                Spacer(modifier = Modifier.width(8.sdp))
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text = item.type,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.ssp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        text = item.timestamp?.let { formatTimestamp(it) }
                            ?: stringResource(id = R.string.not_available),
                        fontSize = 10.ssp
                    )
                }
                Icon(
                    painter = painterResource(
                        id = if (item.isFavorite) R.drawable.ic_favourite else R.drawable.ic_non_favourite
                    ),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(id = R.string.favorite_icon),
                    modifier = Modifier
                        .padding(end = 8.sdp)
                        .clickable {
                            vm.onEvent(ResultScreenEvent.ToggleFavorite(item))
                        }
                )
            }
            Spacer(modifier = Modifier.height(4.sdp))
            Text(
                color = colorResource(R.color.dodger_blue),
                textDecoration = TextDecoration.Underline,
                text = displayText,
                fontSize = 10.ssp,
                modifier = Modifier.padding(4.sdp)
            )
            Spacer(modifier = Modifier.height(4.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.sdp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconWithLabel(
                    iconRes = R.drawable.ic_open,
                    label = stringResource(id = R.string.open),
                    onClick = {
                        adManager.showAd(
                            activity = activity,
                            showAd = vm.remoteConfig.adConfigs.adOnScanOpen,
                            onNext = { vm.onEvent(ResultScreenEvent.OpenItem(item, context)) }
                        )
                    }
                )
                IconWithLabel(
                    iconRes = R.drawable.ic_share,
                    label = stringResource(id = R.string.share),
                    onClick = { vm.onEvent(ResultScreenEvent.ShareItem(item)) }
                )
                IconWithLabel(
                    iconRes = R.drawable.ic_copy,
                    label = stringResource(id = R.string.copy),
                    onClick = { vm.onEvent(ResultScreenEvent.CopyItem(item)) }
                )
            }
        }
    }
}
