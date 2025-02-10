package com.thezayin.scanner.presentation.scanner.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

/**
 * A composable function that displays an icon button with a text label underneath.
 *
 * This component is used for navigation and actions like toggling flashlight,
 * opening gallery, or batch scanning.
 *
 * @param icon Resource ID of the vector drawable for the icon.
 * @param label The text label displayed below the icon.
 * @param onClick Callback triggered when the button is clicked.
 */
@Composable
fun IconButtonWithLabel(
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            modifier = Modifier.size(24.sdp),
            contentDescription = label,
            tint = Color.White
        )

        Spacer(modifier = Modifier.height(2.sdp))

        Text(
            text = label,
            fontSize = 10.ssp,
            fontFamily = FontFamily(Font(com.thezayin.values.R.font.poppins_regular)),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
