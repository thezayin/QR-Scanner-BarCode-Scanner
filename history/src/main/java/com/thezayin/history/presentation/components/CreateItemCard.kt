package com.thezayin.history.presentation.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.framework.utils.formatTimestamp
import com.thezayin.framework.utils.getQrTypeIcon
import com.thezayin.history.domain.model.CreateItem
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun CreateItemCard(
    item: CreateItem,
    onDelete: (CreateItem) -> Unit,
    onItemClicked: (CreateItem) -> Unit
) {

    val iconTint: Color = when (item.title) {
        "Barcode" -> colorResource(R.color.ptcl_green)
        "URL" -> colorResource(R.color.telenor_blue)
        "Email" -> colorResource(R.color.strawberry)
        "Call" -> colorResource(R.color.ptcl_green)
        "SMS" -> colorResource(R.color.ad_button_color)
        "Location" -> colorResource(R.color.red)
        "Calendar" -> colorResource(R.color.astronaut_blue)
        "Contact" -> colorResource(R.color.brown)
        "Text" -> colorResource(R.color.bright_sun)
        "Wifi" -> colorResource(R.color.blue_purple)
        "Clipboard" -> colorResource(R.color.tealish)
        "EAN-8" -> colorResource(R.color.ad_tag_color)
        "EAN-13" -> colorResource(R.color.ad_tag_color)
        "UPC-A" -> colorResource(R.color.ad_tag_color)
        "UPC-E" -> colorResource(R.color.ad_tag_color)
        "Code 39" -> colorResource(R.color.telenor_blue)
        "Code 128" -> colorResource(R.color.telenor_blue)
        "ITF" -> colorResource(R.color.telenor_blue)
        "PDF417" -> colorResource(R.color.telenor_blue)
        "Codabar" -> colorResource(R.color.telenor_blue)
        "DataMatrix" -> colorResource(R.color.deep_lavender)
        "Aztec" -> colorResource(R.color.bright_lavender)
        else -> colorResource(R.color.dodger_blue)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.sdp, horizontal = 8.sdp)
            .clickable { onItemClicked(item) },
        shape = RoundedCornerShape(4.sdp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(15.sdp))
            val qrTypeIcon = getQrTypeIcon(item.title)
            Icon(
                painter = painterResource(id = qrTypeIcon),
                contentDescription = stringResource(id = R.string.qr_type_icon),
                modifier = Modifier.size(20.sdp),
                tint = iconTint,
            )
            Spacer(modifier = Modifier.width(15.sdp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.sdp),
                verticalArrangement = Arrangement.spacedBy(4.sdp)
            ) {
                Spacer(modifier = Modifier.height(8.sdp))
                val jsonObject = try {
                    org.json.JSONObject(item.content)
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    null
                }
                jsonObject?.let { obj ->
                    for (key in obj.keys()) {
                        val value = obj.getString(key)
                        Text(
                            text = value,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily(Font(R.font.roboto_bold)),
                            fontSize = 10.ssp,
                        )
                    }
                }
                Text(
                    fontSize = 8.ssp,
                    text = item.title,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    text = formatTimestamp(item.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 8.ssp
                )
            }
            IconButton(
                onClick = { onDelete(item) },
                modifier = Modifier.padding(start = 4.sdp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = stringResource(id = R.string.delete),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.sdp)
                )
            }
        }
    }
}
