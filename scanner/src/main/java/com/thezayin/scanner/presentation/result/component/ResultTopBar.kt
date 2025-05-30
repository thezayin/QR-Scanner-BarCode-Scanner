package com.thezayin.scanner.presentation.result.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultTopBar(
    isPremium: Boolean, onNavigateUp: () -> Unit, navigateToPremium: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            onNavigateUp()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(id = R.string.back)
            )
        }
        Text(
            text = stringResource(id = R.string.result),
            fontSize = 16.ssp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (!isPremium) {
            IconButton(onClick = {
                navigateToPremium()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_crown),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(15.sdp),
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(30.sdp))
        }
    }
}


@Preview
@Composable
fun ResultTopBarPreview() {
    ResultTopBar(isPremium = false, onNavigateUp = { }, navigateToPremium = { })
}