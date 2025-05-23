package com.thezayin.premium.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.framework.utils.openPrivacy
import com.thezayin.framework.utils.openTerms
import com.thezayin.premium.presentation.components.PlanOption
import com.thezayin.values.R

@Composable
fun PremiumScreen(
    onContinueClick: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf("Weekly") }
    val backgroundPainter =
        painterResource(id = R.drawable.bg_premium) // Set your background image here

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = backgroundPainter,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Unlimited Access\nTo All Features",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                val features = listOf(
                    "Scan QR Codes", "Scan Barcodes", "No Ads and No Limits"
                )

                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.Start // Keeps icon + text aligned on left
                    ) {
                        features.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFFB388FF),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feature, color = Color.White, fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PlanOption(
                    title = "Weekly Plan",
                    price = "Rs 200/week",
                    selected = selectedPlan == "Weekly",
                    onClick = { selectedPlan = "Weekly" })
                Spacer(modifier = Modifier.height(12.dp))
                PlanOption(
                    title = "Annual Plan",
                    price = "Rs 200/week",
                    selected = selectedPlan == "Annual",
                    onClick = { selectedPlan = "Annual" })
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onContinueClick(selectedPlan) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB388FF))
                ) {
                    Text("Continue", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.clickable {
                            context.openTerms()
                        },
                        text = AnnotatedString(
                            text = "Privacy Policy", spanStyles = listOf(
                                AnnotatedString.Range(
                                    item = SpanStyle(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        textDecoration = TextDecoration.Underline
                                    ), start = 0, end = "Privacy Policy".length
                                )
                            )
                        ),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Cancel Anytime",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.clickable {
                            context.openPrivacy()
                        }, text = AnnotatedString(
                            text = "Terms & Conditions", spanStyles = listOf(
                                AnnotatedString.Range(
                                    item = SpanStyle(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        textDecoration = TextDecoration.Underline // Underline the text
                                    ), start = 0, end = "Terms & Conditions".length
                                )
                            )
                        ), textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewPremiumScreen() {
    PremiumScreen { }
}