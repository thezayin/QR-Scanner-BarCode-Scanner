package com.thezayin.qrscanner.ui.premium.presentation.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import com.thezayin.framework.utils.openPrivacy
import com.thezayin.framework.utils.openTerms
import ir.kaaveh.sdpcompose.ssp

@Composable
fun BottomSection(context: Context) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            "Privacy Policy",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 8.ssp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { context.openPrivacy() })
        Text(
            "Cancel Anytime", color = Color.White.copy(alpha = 0.7f), fontSize = 8.ssp
        )
        Text(
            "Terms & Conditions",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 8.ssp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { context.openTerms() })
    }
}
