package com.thezayin.qrscanner.ui.premium.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.qrscanner.ui.premium.domain.model.SubscriptionDetails
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun PlanOption(
    subscription: SubscriptionDetails, selected: Boolean, onClick: () -> Unit
) {
    val borderColor = if (selected) Color(0xFFB388FF) else Color.White.copy(alpha = 0.5f)
    val backgroundColor =
        if (selected) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.3f)
    val title =
        if (subscription.id.contains("weekly")) "Weekly Subscription" else "Yearly Subscription"
    val priceText = if (subscription.id.contains("weekly")) {
        "${subscription.price}/week"
    } else {
        "${subscription.price}/year"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .border(1.sdp, borderColor, shape = RoundedCornerShape(10.sdp))
            .background(backgroundColor, shape = RoundedCornerShape(10.sdp))
            .clickable { onClick() }
            .padding(15.sdp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = title, color = Color.White, fontSize = 10.ssp)
            Text(text = priceText, color = Color.White, fontSize = 10.ssp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewPlanOption() {
    PlanOption(
        subscription = SubscriptionDetails(
            id = "weekly.sub.removeads",
            title = "Weekly Subscription",
            description = "Description of the weekly subscription",
            price = "$5",
            currencyCode = "USD",
            type = "SUBS"
        ), selected = true
    ) {}
}
