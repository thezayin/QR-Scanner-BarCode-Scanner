package com.thezayin.start_up.languages.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.thezayin.start_up.languages.manager.Language
import com.thezayin.start_up.languages.utils.countryCodeToFlagEmoji
import com.thezayin.start_up.languages.utils.languageCodeToCountryCode
import ir.kaaveh.sdpcompose.sdp

@Composable
fun LanguageListItem(language: Language, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected)
        colorResource(com.thezayin.values.R.color.blur_background)
    else
        MaterialTheme.colorScheme.surfaceContainer

    val countryCode = languageCodeToCountryCode(language.locale.value)
    val flagEmoji = countryCodeToFlagEmoji(countryCode)

    Card(
        modifier = Modifier
            .padding(vertical = 5.sdp, horizontal = 10.sdp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.sdp)
                .padding(10.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = flagEmoji, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(10.sdp))
            Text(
                text = language.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}



