package com.thezayin.scanner.presentation.result.component

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.presentation.result.ResultScreenViewModel
import com.thezayin.scanner.presentation.result.event.ResultScreenEvent
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun ProductFoundCard(
    item: ResultScreenItem,
    scannedResult: String,
    sessionImageUri: String,
    vm: ResultScreenViewModel
) {
    val context = LocalContext.current
    val activity = context as Activity
    val adManager = vm.adManager

    Card(
        modifier = Modifier.padding(horizontal = 10.sdp, vertical = 20.sdp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        Column(modifier = Modifier.padding(8.sdp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.imageUrl ?: sessionImageUri,
                    contentDescription = stringResource(id = R.string.product_image_desc),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.sdp)
                )
                Spacer(modifier = Modifier.width(8.sdp))
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text = stringResource(
                            id = R.string.product_name,
                            item.name ?: stringResource(id = R.string.unknown_product)
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.ssp
                    )
                    Text(
                        text = scannedResult,
                        fontSize = 8.ssp,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    painter = painterResource(
                        id = if (item.isFavorite) R.drawable.ic_favourite else R.drawable.ic_non_favourite
                    ),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "Favorite Icon",
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
                    .padding(horizontal = 4.sdp, vertical = 10.sdp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconWithLabel(
                    iconRes = R.drawable.ic_barcode,
                    label = stringResource(id = R.string.view_code),
                    onClick = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.view_code_to_be_implemented),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                IconWithLabel(
                    iconRes = R.drawable.ic_amazon,
                    label = stringResource(id = R.string.amazon),
                    onClick = {
                        val query = Uri.encode(scannedResult)
                        val url = "https://www.amazon.com/s?k=$query"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                )
                IconWithLabel(
                    iconRes = R.drawable.ic_open,
                    label = stringResource(id = R.string.open),
                    onClick = {
                        adManager.showAd(
                            activity = activity,
                            showAd = vm.remoteConfig.adConfigs.adOnScanOpen,
                            onNext = {
                                val url = "https://world.openfoodfacts.org/product/$scannedResult"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            }
                        )

                    })
                IconWithLabel(
                    iconRes = R.drawable.ic_ebay,
                    label = stringResource(id = R.string.ebay),
                    onClick = {
                        val query = Uri.encode(scannedResult)
                        val url = "https://www.ebay.com/sch/i.html?_nkw=$query"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    })
                IconWithLabel(
                    iconRes = R.drawable.ic_share,
                    label = stringResource(id = R.string.share),
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, scannedResult)
                        }
                        val chooserIntent =
                            Intent.createChooser(shareIntent, context.getString(R.string.share_via))
                                .apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                        context.startActivity(chooserIntent)
                    })
                IconWithLabel(
                    iconRes = R.drawable.ic_shop_now,
                    label = stringResource(id = R.string.shop_now),
                    onClick = {
                        val query = Uri.encode(scannedResult)
                        val url = "https://www.google.com/search?tbm=shop&q=$query"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    })
                IconWithLabel(
                    iconRes = R.drawable.ic_copy,
                    label = stringResource(id = R.string.copy),
                    onClick = {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            context.getString(R.string.scanned_result), scannedResult
                        )
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            context,
                            context.getString(R.string.copied_to_clipboard),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
            }
        }
    }
}