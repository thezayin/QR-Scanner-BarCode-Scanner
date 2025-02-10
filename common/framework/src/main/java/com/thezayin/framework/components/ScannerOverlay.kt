package com.thezayin.framework.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.kaaveh.sdpcompose.sdp

/**
 * A visual overlay for the QR Scanner, including a scanning animation.
 *
 * This component consists of:
 * - A **square scanning area** at the center of the screen.
 * - **Four corner indicators** to visually highlight the scanning region.
 * - A **moving red scanning line** that animates up and down.
 *
 * @param modifier Modifier for styling and layout adjustments.
 */
@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val linePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(25.sdp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.sdp)
        ) {
            val squareSize = size.minDimension * 0.7f
            val left = (size.width - squareSize) / 2
            val top = (size.height - squareSize) / 2
            val right = left + squareSize
            val bottom = top + squareSize
            val cornerLength = 40.dp.toPx()
            val cornerStrokeWidth = 2.dp.toPx()
            drawLine(
                color = Color.Blue,
                start = Offset(left, top),
                end = Offset(left + cornerLength, top),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.Blue,
                start = Offset(left, top),
                end = Offset(left, top + cornerLength),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.Blue,
                start = Offset(right, top),
                end = Offset(right - cornerLength, top),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.Blue,
                start = Offset(right, top),
                end = Offset(right, top + cornerLength),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.Blue,
                start = Offset(left, bottom),
                end = Offset(left + cornerLength, bottom),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.Blue,
                start = Offset(left, bottom),
                end = Offset(left, bottom - cornerLength),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.Blue,
                start = Offset(right, bottom),
                end = Offset(right - cornerLength, bottom),
                strokeWidth = cornerStrokeWidth
            )
            drawLine(
                color = Color.Blue,
                start = Offset(right, bottom),
                end = Offset(right, bottom - cornerLength),
                strokeWidth = cornerStrokeWidth
            )
            val lineY = top + squareSize * linePosition
            drawLine(
                color = Color.Red,
                start = Offset(left, lineY),
                end = Offset(right, lineY),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}