package com.thezayin.scanner.presentation.result.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.thezayin.scanner.presentation.result.ResultScreenViewModel
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun ViewBarcodeDialog(
    viewModel: ResultScreenViewModel,
    barcodeText: String,
    onDismiss: () -> Unit,
    onCopy: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier.padding(16.sdp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = stringResource(R.string.close_dialog)
                        )
                    }
                }

                Spacer(Modifier.height(8.sdp))

                Text(
                    text = barcodeText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.ssp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.sdp))

                Button(
                    onClick = { onCopy(barcodeText) }, colors = ButtonDefaults.buttonColors(
                        containerColor = viewModel.preferencesManager.getPrimaryColor(),
                    )
                ) {
                    Text(stringResource(R.string.copy_it), fontSize = 12.ssp)
                }
            }
        }
    }
}