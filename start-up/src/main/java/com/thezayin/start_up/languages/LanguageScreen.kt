package com.thezayin.start_up.languages

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.thezayin.framework.components.AdLoadingDialog
import com.thezayin.framework.components.ComposableLifecycle
import com.thezayin.framework.components.GoogleNativeSimpleAd
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    onLanguageSelection: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LanguageViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val showLoadingAd = remember { mutableStateOf(false) }

    if (showLoadingAd.value) {
        AdLoadingDialog()
    }

    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                scope.coroutineContext.cancelChildren()
                scope.launch {
                    while (this.isActive) {
                        viewModel.getNativeAd(context)
                        delay(20000L)
                    }
                }
            }

            else -> Unit
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    navigationIcon = {
                        IconButton(onClick = { onNavigateBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = { Text(text = "Select Language") }
                )
            }
        },
        bottomBar = {
            GoogleNativeSimpleAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(150.dp),
                nativeAd = viewModel.nativeAd.value
            )
        },
        content = { paddingValues ->
            when (state) {
                is LanguageState.Initialization -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LanguageState.Content -> {
                    val content = state as LanguageState.Content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items(content.languages) { language ->
                            LanguageListItem(
                                language = language,
                                isSelected = language == content.selectedLang,
                                onClick = {
                                    viewModel.onLanguageSelected(language)
                                    onLanguageSelection()
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LanguageListItem(language: Language, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected)
        colorResource(com.thezayin.values.R.color.successful_color)
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
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = language.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Maps a language code to a country code for displaying the flag.
 */
fun languageCodeToCountryCode(languageCode: String): String {
    return when (languageCode.lowercase()) {
        "en" -> "US"
        "fr" -> "FR"
        "es" -> "ES"
        "ar" -> "SA"
        "ru" -> "RU"
        "pt" -> "BR"
        "hi" -> "IN"
        "da" -> "DK"
        "it" -> "IT"
        "tr" -> "TR"
        "id" -> "ID"
        "ja" -> "JP"
        "ko" -> "KR"
        "pl" -> "PL"
        "af" -> "ZA"
        "zh" -> "CN"
        "zh-tw" -> "TW"
        "th" -> "TH"
        "fa" -> "IR"
        "vi" -> "VN"
        "hu" -> "HU"
        "he" -> "IL"
        "sv" -> "SE"
        "no" -> "NO"
        "ca" -> "ES"
        "ms" -> "MY"
        "nl" -> "NL"
        "cs" -> "CZ"
        "ur" -> "PK"
        "de" -> "DE"
        "uk" -> "UA"
        "bn" -> "BD"
        "hy" -> "AM"
        "ro" -> "RO"
        else -> "US"
    }
}

fun countryCodeToFlagEmoji(countryCode: String): String {
    if (countryCode.length != 2) return countryCode
    val firstChar = countryCode[0].uppercaseChar() - 'A' + 0x1F1E6
    val secondChar = countryCode[1].uppercaseChar() - 'A' + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}