package com.thezayin.start_up.setting.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ColorGrid(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val firstRow = colors.take(6)
    val secondRow = colors.drop(6)

    Column(verticalArrangement = Arrangement.spacedBy(8.sdp)) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.sdp)
        ) {
            firstRow.forEach { color ->
                ColorCard(
                    color = color,
                    isSelected = (color == selectedColor),
                    onClick = { onColorSelected(color) }
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.sdp)
        ) {
            secondRow.forEach { color ->
                ColorCard(
                    color = color,
                    isSelected = (color == selectedColor),
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}
