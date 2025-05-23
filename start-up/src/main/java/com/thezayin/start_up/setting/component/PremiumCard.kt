package com.thezayin.start_up.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Preview
@Composable
fun PremiumCard(
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.sdp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.sdp, max = 150.sdp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colorResource(R.color.bubblegum),
                            colorResource(R.color.ad_button_color),
                            colorResource(R.color.bluishRandom),
                        )
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(10.sdp),
            ) {
                Text(
                    text = "Upgrade to Premium",
                    fontSize = 18.ssp,
                    color = colorResource(R.color.white),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.sdp))
                Text(
                    text = "- Enjoy an ad-free experience",
                    fontSize = 8.ssp,
                    color = colorResource(R.color.white),
                )
                Spacer(modifier = Modifier.height(4.sdp))
                Text(
                    text = "- Unlimited scans at your fingertips",
                    fontSize = 8.ssp,
                    color = colorResource(R.color.white),
                )
                Spacer(modifier = Modifier.height(4.sdp))
                Text(
                    text = "- 24/7 dedicated support",
                    fontSize = 8.ssp,
                    color = colorResource(R.color.white),
                )
                Spacer(modifier = Modifier.height(4.sdp))
                Text(
                    text = "- Generate unlimited QR codes",
                    fontSize = 8.ssp,
                    color = colorResource(R.color.white),
                )
            }
            Icon(
                modifier = Modifier.wrapContentSize(),
                painter = painterResource(R.drawable.ic_tech),
                contentDescription = "Tech icon",
                tint = colorResource(R.color.white),
            )
        }
    }
}
