package com.thezayin.scanner.presentation.result.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ResultTopBar(
    onNavigateUp: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 10.sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        Icon(
            modifier = Modifier
                .size(20.sdp)
                .clickable { onNavigateUp() },
            tint = MaterialTheme.colorScheme.onSurface,
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = stringResource(R.string.back)
        )
    }
}


@Preview
@Composable
fun ResultTopBarPreview() {
    ResultTopBar(
        onNavigateUp = { },
    )
}