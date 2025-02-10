package com.thezayin.start_up.splash.component

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun BottomText(modifier: Modifier, text: Int = R.string.loading_settings) {
    val activity = LocalContext.current as Activity
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(end = 5.sdp)
                .size(10.sdp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            textAlign = TextAlign.Center,
            text = activity.getString(text),
            fontSize = 9.ssp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
        )
    }
}
