package com.thezayin.generate.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    validation: (String) -> Boolean = { true }
) {
    val error = !validation(value)
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 10.ssp,
            fontFamily = FontFamily(Font(R.font.poppins_regular))
        ),
        placeholder = {
            Text(
                text = placeholder.ifBlank { label },
                fontSize = 10.ssp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily(Font(R.font.poppins_regular))
            )
        },
        isError = error,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedIndicatorColor = if (error) Color.Red else Color.Transparent,
            unfocusedIndicatorColor = if (error) Color.Red else Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        ),
        singleLine = true,
        shape = RoundedCornerShape(4.sdp),
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
    )
}