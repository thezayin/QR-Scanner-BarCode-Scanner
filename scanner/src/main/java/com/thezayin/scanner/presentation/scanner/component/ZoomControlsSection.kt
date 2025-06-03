package com.thezayin.scanner.presentation.scanner.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomControlsSection(
    primaryColor: Color,
    zoomLevel: Float,
    minZoomRatio: Float,
    maxZoomRatio: Float,
    onZoomChange: (Float) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.sdp, vertical = 30.sdp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.sdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onZoomOut) {
                Icon(
                    tint = Color.White,
                    modifier = Modifier.size(18.sdp),
                    painter = painterResource(id = R.drawable.ic_zoom_out),
                    contentDescription = stringResource(R.string.zoom_out)
                )
            }
            Slider(
                value = zoomLevel,
                onValueChange = onZoomChange,
                valueRange = minZoomRatio..maxZoomRatio,
                modifier = Modifier
                    .padding(horizontal = 8.sdp)
                    .weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = primaryColor,
                    activeTrackColor = primaryColor,
                    inactiveTrackColor = primaryColor.copy(0.3f)
                ),
                track = { sliderPositions ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.sdp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        val currentFraction =
                            (sliderPositions.value - minZoomRatio) / (maxZoomRatio - minZoomRatio)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    fraction = currentFraction.coerceIn(
                                        0f,
                                        1f
                                    )
                                )
                                .height(3.sdp)
                                .background(primaryColor)
                        )
                    }
                },
                thumb = {
                    Canvas(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(12.sdp)
                    ) {
                        drawCircle(
                            color = Color.White,
                            radius = size.minDimension / 2
                        )
                        drawCircle(
                            color = Color.White,
                            radius = size.minDimension / 2,
                            style = Stroke(width = 4f)
                        )
                    }
                }
            )

            IconButton(onClick = onZoomIn) {
                Icon(
                    modifier = Modifier.size(15.sdp),
                    tint = Color.White,
                    painter = painterResource(id = R.drawable.ic_zoom_in),
                    contentDescription = stringResource(R.string.zoom_in)
                )
            }
        }
    }
}