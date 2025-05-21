package com.thezayin.start_up.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.thezayin.start_up.setting.component.SettingsScreenContent
import com.thezayin.start_up.setting.event.SettingsEvent
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    navigateToLanguage: () -> Unit,
    navigateToPremium: () -> Unit,
    onNavigateBack: () -> Unit,
    navigateToFavourite: () -> Unit
) {
    val viewModel: SettingsViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    SettingsScreenContent(
        state = state,
        navigateToLanguage = navigateToLanguage,
        navigateToPremium = navigateToPremium,
        onColorSelected = { color ->
            viewModel.onEvent(SettingsEvent.ColorSelected(color))
        },
        onBeepToggled = { viewModel.onEvent(SettingsEvent.BeepToggled(it)) },
        onVibrateToggled = { viewModel.onEvent(SettingsEvent.VibrateToggled(it)) },
        onDarkThemeToggled = { viewModel.onEvent(SettingsEvent.DarkThemeToggled(it)) },
        onFavouritesClicked = navigateToFavourite,
        onNavigateBack = onNavigateBack
    )
}
