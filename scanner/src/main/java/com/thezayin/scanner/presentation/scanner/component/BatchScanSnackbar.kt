package com.thezayin.scanner.presentation.scanner.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun BatchScanSnackbar(
    modifier: Modifier = Modifier,
    scannedCount: Int,
    onCancelBatch: () -> Unit,
    onConfirmBatch: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.sdp, vertical = 5.sdp),
        shape = RoundedCornerShape(10.sdp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.black_0_3)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.sdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCancelBatch) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cancel_batch_scan),
                    tint = colorResource(R.color.white)
                )
            }
            Text(
                text = stringResource(R.string.total_scanned, scannedCount),
                color = colorResource(R.color.white),
                fontSize = 12.ssp,
                style = MaterialTheme.typography.titleMedium
            )
            if (scannedCount > 0) {
                IconButton(onClick = onConfirmBatch) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.confirm_batch_scan),
                        tint = colorResource(R.color.white)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.sdp))
            }
        }
    }
}