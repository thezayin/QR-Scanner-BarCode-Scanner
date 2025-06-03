package com.thezayin.history.presentation.components

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.net.toUri
import com.thezayin.framework.utils.formatTimestamp
import com.thezayin.framework.utils.typeToIcon
import com.thezayin.history.domain.model.ScanItem
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun ScanItemCard(
    item: ScanItem,
    onItemClick: (ScanItem) -> Unit,
    onToggleFavorite: (ScanItem) -> Unit,
    onDelete: (ScanItem) -> Unit
) {
    val context = LocalContext.current
    val iconTint: Color = when (item.type) {
        "Barcode" -> colorResource(R.color.ptcl_green)
        "URL" -> colorResource(R.color.telenor_blue)
        "Email" -> colorResource(R.color.red)
        "Phone" -> colorResource(R.color.ptcl_green)
        "SMS" -> colorResource(R.color.ad_button_color)
        "Location" -> colorResource(R.color.red)
        "Calendar" -> colorResource(R.color.astronaut_blue)
        else -> colorResource(R.color.dodger_blue)
    }

    Card(
        modifier = Modifier
            .clickable { onItemClick(item) }
            .fillMaxWidth()
            .padding(vertical = 4.sdp, horizontal = 8.sdp),
        shape = RoundedCornerShape(4.sdp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        Box(
            modifier = Modifier.padding(12.sdp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.sdp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = typeToIcon(item.type)),
                    contentDescription = stringResource(id = R.string.type_icon),
                    tint = iconTint,
                    modifier = Modifier.size(25.sdp)
                )

                Spacer(modifier = Modifier.width(8.sdp))
                Column(modifier = Modifier.weight(1f)) {
                    when (item.type) {
                        "CALL" -> {
                            Text(
                                text = item.scannedText,
                                color = colorResource(R.color.dodger_blue),
                                fontSize = 10.ssp,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    val intent =
                                        Intent(Intent.ACTION_DIAL, item.scannedText.toUri())
                                    context.startActivity(intent)
                                }
                            )
                        }

                        "LOCATION" -> {
                            val parts = item.scannedText.split(",")
                            if (parts.size >= 2) {
                                Text(
                                    text = stringResource(
                                        id = R.string.location_text,
                                        parts[0],
                                        parts[1]
                                    ),
                                    color = colorResource(R.color.dodger_blue),
                                    fontSize = 10.ssp,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier.clickable {
                                        val uri = "geo:${item.scannedText}"
                                        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }

                        "CALENDAR" -> {
                            val eventDetails = item.scannedText.split(",")
                            if (eventDetails.isNotEmpty()) {
                                Text(
                                    text = stringResource(
                                        id = R.string.event_text,
                                        eventDetails[0]
                                    ),
                                    color = colorResource(R.color.dodger_blue),
                                    fontSize = 10.ssp,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier.clickable {
                                        val intent = Intent(Intent.ACTION_INSERT)
                                        intent.type = "vnd.android.cursor.item/event"
                                        intent.putExtra(
                                            CalendarContract.Events.TITLE,
                                            eventDetails[0]
                                        )
                                        if (eventDetails.size >= 2) {
                                            intent.putExtra(
                                                CalendarContract.Events.EVENT_LOCATION,
                                                eventDetails[1]
                                            )
                                        }
                                        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }

                        "SMS" -> {
                            Text(
                                text = item.scannedText,
                                color = colorResource(R.color.dodger_blue),
                                fontSize = 10.ssp,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    val intent =
                                        Intent(Intent.ACTION_VIEW, item.scannedText.toUri())
                                    context.startActivity(intent)
                                }
                            )
                        }

                        else -> {
                            Text(
                                text = item.scannedText,
                                color = colorResource(R.color.dodger_blue),
                                fontSize = 10.ssp,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                    Text(
                        text = item.type,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 8.ssp
                    )
                    Text(
                        text = formatTimestamp(item.timestamp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 8.ssp
                    )


                }

                Row {
                    Icon(
                        painter = painterResource(
                            id = if (item.isFavorite) R.drawable.ic_favourite else R.drawable.ic_non_favourite
                        ),
                        tint = colorResource(R.color.red),
                        contentDescription = stringResource(id = R.string.favorite_icon),
                        modifier = Modifier
                            .size(20.sdp)
                            .clickable { onToggleFavorite(item) }
                    )

                    Spacer(modifier = Modifier.width(8.sdp))
                    Icon(
                        modifier = Modifier
                            .size(20.sdp)
                            .clickable { onDelete(item) },
                        tint = MaterialTheme.colorScheme.onSurface,
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(id = R.string.delete)
                    )
                }
            }
        }
    }
}
