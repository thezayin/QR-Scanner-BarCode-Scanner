package com.thezayin.qrscanner.ui.premium.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun SubscriptionButton(
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Button(
        enabled = enabled,
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(40.sdp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color(0xFFB388FF),
            disabledContainerColor = Color.Gray,
        ),
        shape = RoundedCornerShape(12.sdp)
    ) {
        Text(text = "Subscribe Now", fontSize = 12.ssp, color = Color.White)
    }
}
