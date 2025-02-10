package com.thezayin.start_up.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ColorCard(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderWidth = if (isSelected) 2.sdp else 0.sdp

    Spacer(
        modifier = Modifier
            .size(40.sdp)
            .background(color = color)
            .clip(RoundedCornerShape(10.sdp))
            .border(borderWidth, MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onClick() }
    )
}