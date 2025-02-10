package com.thezayin.start_up.setting.state

import androidx.compose.ui.graphics.Color

data class SettingsState(
    val primaryColor: Color = Color(0xFFA761FF),
    val beepEnabled: Boolean = false,
    val vibrateEnabled: Boolean = false,
    val darkThemeEnabled: Boolean = false
)