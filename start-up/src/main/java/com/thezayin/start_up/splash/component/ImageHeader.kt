package com.thezayin.start_up.splash.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ImageHeader(modifier: Modifier) {
    Card(
        modifier = modifier.size(120.sdp),
        shape = RoundedCornerShape(0.sdp)
    ) {
        Image(
            painter = painterResource(id = com.thezayin.values.R.drawable.ic_main),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}