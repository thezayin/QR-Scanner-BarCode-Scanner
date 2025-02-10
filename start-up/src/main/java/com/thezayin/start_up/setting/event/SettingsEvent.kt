package com.thezayin.start_up.setting.event

import androidx.compose.ui.graphics.Color

sealed class SettingsEvent {
    data class ColorSelected(val color: Color) : SettingsEvent()
    data class BeepToggled(val enabled: Boolean) : SettingsEvent()
    data class VibrateToggled(val enabled: Boolean) : SettingsEvent()
    data class DarkThemeToggled(val enabled: Boolean) : SettingsEvent()
}
