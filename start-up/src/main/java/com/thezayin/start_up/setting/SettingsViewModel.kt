package com.thezayin.start_up.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.start_up.setting.event.SettingsEvent
import com.thezayin.start_up.setting.state.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsState(
            primaryColor = preferencesManager.getPrimaryColor(),
            beepEnabled = preferencesManager.getBeepEnabled(),
            vibrateEnabled = preferencesManager.getVibrateEnabled(),
            darkThemeEnabled = preferencesManager.getDarkThemeEnabled()
        )
    )
    val state: StateFlow<SettingsState> = _state

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ColorSelected -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(primaryColor = event.color)
                    preferencesManager.setPrimaryColor(event.color)
                }
            }

            is SettingsEvent.BeepToggled -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(beepEnabled = event.enabled)
                    preferencesManager.setBeepEnabled(event.enabled)
                }
            }

            is SettingsEvent.VibrateToggled -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(vibrateEnabled = event.enabled)
                    preferencesManager.setVibrateEnabled(event.enabled)
                }
            }

            is SettingsEvent.DarkThemeToggled -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(darkThemeEnabled = event.enabled)
                    preferencesManager.setDarkThemeEnabled(event.enabled)
                }
            }
        }
    }
}
