package com.thezayin.scanner.presentation.scanner.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

/**
 * A header section displaying key action buttons such as Batch Scan, Gallery Selection, and Flashlight Toggle.
 *
 * This component is placed at the top of the screen inside a rounded card for better UI aesthetics.
 *
 * @param onBatchClick Callback triggered when the Batch button is clicked.
 * @param onGalleryClick Callback triggered when the Gallery button is clicked.
 * @param onFlashToggle Callback triggered when the Flashlight button is toggled.
 * @param isFlashOn Boolean indicating the current state of the flashlight.
 */
@Composable
fun HeaderSection(
    onBatchClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFlashToggle: () -> Unit,
    isFlashOn: Boolean
) {
    Card(
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 30.sdp)
            .statusBarsPadding(),
        shape = RoundedCornerShape(10.sdp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.semi_black)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.sdp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButtonWithLabel(
                icon = R.drawable.ic_batch,
                label = stringResource(id = R.string.batch_scan),
                onClick = onBatchClick
            )

            IconButtonWithLabel(
                icon = R.drawable.ic_gallery,
                label = stringResource(id = R.string.gallery),
                onClick = onGalleryClick
            )

            IconButtonWithLabel(
                icon = R.drawable.ic_flashlight,
                label = if (isFlashOn) stringResource(id = R.string.flash_on) else stringResource(id = R.string.flash_off),
                onClick = onFlashToggle
            )
        }
    }
}
