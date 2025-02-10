package com.thezayin.scanner.presentation.result.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.thezayin.framework.utils.parseWifiResult
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.presentation.result.ResultScreenViewModel
import com.thezayin.scanner.presentation.result.event.ResultScreenEvent
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun WifiResultCard(
    item: ResultScreenItem,
    vm: ResultScreenViewModel
) {
    val wifiInfo = parseWifiResult(item.result)
    val ssid = wifiInfo?.first ?: stringResource(id = R.string.unknown_ssid)
    val password = wifiInfo?.second ?: stringResource(id = R.string.unknown_password)
    Card(modifier = Modifier.padding(horizontal = 10.sdp, vertical = 20.sdp)) {
        Column(modifier = Modifier.padding(8.sdp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = stringResource(id = R.string.product_image_desc),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.sdp)
                )
                Spacer(modifier = Modifier.width(8.sdp))
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text = stringResource(id = R.string.wifi_ssid, ssid),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.ssp
                    )
                    Text(
                        text = stringResource(id = R.string.wifi_password, password),
                        fontSize = 10.ssp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                androidx.compose.material3.Icon(
                    painter = painterResource(
                        id = if (item.isFavorite) R.drawable.ic_favourite else R.drawable.ic_non_favourite
                    ),
                    contentDescription = stringResource(id = R.string.favorite_icon),
                    modifier = Modifier
                        .padding(end = 8.sdp)
                        .clickable {
                            vm.onEvent(ResultScreenEvent.ToggleFavorite(item))
                        }
                )
            }
            Spacer(modifier = Modifier.height(8.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.sdp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconWithLabel(
                    iconRes = R.drawable.ic_connect,
                    label = stringResource(id = R.string.connect),
                    onClick = { vm.onEvent(ResultScreenEvent.ConnectWifi(item)) }
                )
                IconWithLabel(
                    iconRes = R.drawable.ic_copy,
                    label = stringResource(id = R.string.copy_password),
                    onClick = { vm.onEvent(ResultScreenEvent.CopyPassword(item, password)) }
                )
                IconWithLabel(
                    iconRes = R.drawable.ic_share,
                    label = stringResource(id = R.string.share),
                    onClick = { vm.onEvent(ResultScreenEvent.ShareWifi(item, ssid, password)) }
                )
                IconWithLabel(
                    iconRes = R.drawable.ic_copy,
                    label = stringResource(id = R.string.copy),
                    onClick = { vm.onEvent(ResultScreenEvent.CopyWifi(item, ssid, password)) }
                )
            }
        }
    }
}
