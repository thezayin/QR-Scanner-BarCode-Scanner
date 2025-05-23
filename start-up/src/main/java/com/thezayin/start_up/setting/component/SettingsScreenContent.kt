package com.thezayin.start_up.setting.component

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.thezayin.framework.utils.openPrivacy
import com.thezayin.framework.utils.openTerms
import com.thezayin.start_up.setting.state.SettingsState
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun SettingsScreenContent(
    state: SettingsState,
    navigateToLanguage: () -> Unit,
    navigateToPremium: () -> Unit,
    onColorSelected: (Color) -> Unit,
    onBeepToggled: (Boolean) -> Unit,
    onVibrateToggled: (Boolean) -> Unit,
    onDarkThemeToggled: (Boolean) -> Unit,
    onFavouritesClicked: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, topBar = {
            SettingTopBar(onNavigateBack = onNavigateBack)
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PremiumCard(
                onClick = navigateToPremium
            )
            Spacer(modifier = Modifier.size(10.sdp))
            Text(
                text = stringResource(id = R.string.color_scheme),
                fontWeight = FontWeight.Bold,
                fontSize = 12.ssp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            ColorGrid(
                colors = listOf(
                    Color(0xFFA761FF),
                    Color(0xFFFE756B),
                    Color(0xFFFF973E),
                    Color(0xFF4DADF9),
                    Color(0xFFFF4045),
                    Color(0xFF00BF9E),
                    Color(0xFFFF7457),
                    Color(0xFF676DE7),
                    Color(0xFFB57188),
                    Color(0xFFFFB200),
                    Color(0xFF34A855),
                    Color(0xFF3CA8C4)
                ), selectedColor = state.primaryColor, onColorSelected = onColorSelected
            )

            SettingsSwitchRow(
                icon = painterResource(R.drawable.ic_music),
                label = stringResource(id = R.string.beep),
                checked = state.beepEnabled,
                onCheckedChange = onBeepToggled
            )
            SettingsSwitchRow(
                icon = painterResource(R.drawable.ic_vibrate),
                label = stringResource(id = R.string.vibrate),
                checked = state.vibrateEnabled,
                onCheckedChange = onVibrateToggled
            )
            SettingsSwitchRow(
                icon = painterResource(R.drawable.ic_theme),
                label = stringResource(id = R.string.dark_theme),
                checked = state.darkThemeEnabled,
                onCheckedChange = onDarkThemeToggled
            )

            SettingsClickableRow(
                icon = painterResource(R.drawable.ic_language),
                label = stringResource(id = R.string.language),
                trailingText = stringResource(id = R.string.english)
            ) {
                navigateToLanguage()
            }

            SettingsClickableRow(
                icon = painterResource(R.drawable.ic_start),
                label = stringResource(id = R.string.favourites),
                trailingText = ""
            ) {
                onFavouritesClicked()
            }
            SettingsClickableRow(
                icon = painterResource(R.drawable.ic_terms),
                label = stringResource(R.string.terms_conditions),
                trailingText = ""
            ) {
                context.openTerms()
            }
            SettingsClickableRow(
                icon = painterResource(R.drawable.ic_privacy),
                label = stringResource(R.string.privacy_policy),
                trailingText = ""
            ) {
                context.openPrivacy()
            }
            SettingsClickableRow(
                icon = painterResource(R.drawable.ic_about_us),
                label = stringResource(R.string.about_us),
                trailingText = ""
            ) {
                val url = "https://bougielabs.com"
                val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }
    }
}