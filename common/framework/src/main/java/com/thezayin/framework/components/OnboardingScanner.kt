package com.thezayin.framework.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ir.kaaveh.sdpcompose.sdp
import kotlin.math.min

/**
 * A visual overlay for the QR Scanner where the image fits *inside* the overlay.
 * The overlay consists of:
 *  - A centered square region (70% of min dimension).
 *  - An [Image] that fills exactly that square region.
 *  - Four corner lines on the edges of that square.
 *  - A red scanning line animating from top to bottom.
 *
 * @param painter The [Painter] for the image that should appear within the overlay.
 * @param modifier Additional layout or styling [Modifier].
 */
@Composable
fun ScannerOverlay(
    painter: Painter,
    modifier: Modifier = Modifier
) {
    // Infinite transition to animate the scan line
    val infiniteTransition = rememberInfiniteTransition()
    val linePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(25.sdp),
        contentAlignment = Alignment.Center
    ) {
        // Calculate the side length of the scanning box
        val boxSide = min(maxWidth.value, maxHeight.value) * 0.5f
        val boxSideDp = boxSide.dp

        // This Box represents the scanning region:
        // - The image is placed at the bottom layer.
        // - The corner lines and animated line are drawn on top.
        Box(
            modifier = Modifier
                .size(boxSideDp) // square region in the center
        ) {
            // 1) The image fills the entire scanning box
            Image(
                painter = painter,
                contentDescription = "Scanner background image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 2) Draw the corners and the scanning line
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // --- Corner parameters ---
                val cornerLength = 40.dp.toPx()
                val cornerStrokeWidth = 2.dp.toPx()

                // --- Draw corners ---
                // Top-left corner
                drawLine(
                    color = Color.Blue,
                    start = Offset(0f, 0f),
                    end = Offset(cornerLength, 0f),
                    strokeWidth = cornerStrokeWidth
                )
                drawLine(
                    color = Color.Blue,
                    start = Offset(0f, 0f),
                    end = Offset(0f, cornerLength),
                    strokeWidth = cornerStrokeWidth
                )

                // Top-right corner
                drawLine(
                    color = Color.Blue,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width - cornerLength, 0f),
                    strokeWidth = cornerStrokeWidth
                )
                drawLine(
                    color = Color.Blue,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, cornerLength),
                    strokeWidth = cornerStrokeWidth
                )

                // Bottom-left corner
                drawLine(
                    color = Color.Blue,
                    start = Offset(0f, size.height),
                    end = Offset(cornerLength, size.height),
                    strokeWidth = cornerStrokeWidth
                )
                drawLine(
                    color = Color.Blue,
                    start = Offset(0f, size.height),
                    end = Offset(0f, size.height - cornerLength),
                    strokeWidth = cornerStrokeWidth
                )

                // Bottom-right corner
                drawLine(
                    color = Color.Blue,
                    start = Offset(size.width, size.height),
                    end = Offset(size.width - cornerLength, size.height),
                    strokeWidth = cornerStrokeWidth
                )
                drawLine(
                    color = Color.Blue,
                    start = Offset(size.width, size.height),
                    end = Offset(size.width, size.height - cornerLength),
                    strokeWidth = cornerStrokeWidth
                )

                // --- Animated scanning line ---
                val lineY = size.height * linePosition
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}
