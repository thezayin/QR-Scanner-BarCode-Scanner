package com.example.premium.presentation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier

@Composable
fun PlanOption(
    title: String,
    price: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) Color(0xFFB388FF) else Color.White.copy(alpha = 0.5f)
    val backgroundColor = if (selected) Color.White.copy(alpha = 0.1f) else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .border(2.dp, borderColor, shape = RoundedCornerShape(12.dp))
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = title, color = Color.White, fontSize = 16.sp)
            Text(text = price, color = Color.White, fontSize = 16.sp)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewPlanOption(){
    PlanOption("Weekly","15", true) { }
}