package com.thezayin.framework.preferences

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class PreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_PREMIUM = "is_premium"
        private const val KEY_PRIMARY_COLOR = "primary_color"
        private const val KEY_BEEP_ENABLED = "beep_enabled"
        private const val KEY_VIBRATE_ENABLED = "vibrate_enabled"
        private const val KEY_DARK_THEME_ENABLED = "dark_theme_enabled"
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val KEY_IS_FIRST_TIME = "is_first_time"
        private val DEFAULT_PRIMARY_COLOR = Color(0xFFA761FF)
    }

    private val _primaryColorFlow = MutableStateFlow(getPrimaryColorFromPrefs())
    val primaryColorFlow = _primaryColorFlow.asStateFlow()

    private val _isPremiumFlow = MutableStateFlow(prefs.getBoolean(KEY_IS_PREMIUM, false))
    val isPremiumFlow: StateFlow<Boolean> = _isPremiumFlow.asStateFlow()

    fun updatePremiumStatus(isPremium: Boolean) {
        prefs.edit { putBoolean(KEY_IS_PREMIUM, isPremium) }
        _isPremiumFlow.value = isPremium
    }

    private val _darkThemeFlow = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME_ENABLED, false))
    val darkThemeFlow = _darkThemeFlow.asStateFlow()

    private val _beepFlow = MutableStateFlow(prefs.getBoolean(KEY_BEEP_ENABLED, false))
    val beepFlow = _beepFlow.asStateFlow()

    private val _vibrateFlow = MutableStateFlow(prefs.getBoolean(KEY_VIBRATE_ENABLED, false))
    val vibrateFlow = _vibrateFlow.asStateFlow()

    private val _selectedLanguageFlow = MutableStateFlow(getSelectedLanguageFromPrefs())
    val selectedLanguageFlow = _selectedLanguageFlow.asStateFlow()

    private fun getPrimaryColorFromPrefs(): Color {
        val colorInt = prefs.getInt(KEY_PRIMARY_COLOR, DEFAULT_PRIMARY_COLOR.toArgb())
        return Color(colorInt)
    }

    fun getPrimaryColor(): Color = _primaryColorFlow.value

    fun setPrimaryColor(color: Color) {
        prefs.edit { putInt(KEY_PRIMARY_COLOR, color.toArgb()) }
        _primaryColorFlow.value = color
    }

    fun getBeepEnabled(): Boolean = _beepFlow.value

    fun setBeepEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_BEEP_ENABLED, enabled) }
        _beepFlow.value = enabled
    }

    fun getVibrateEnabled(): Boolean = _vibrateFlow.value

    fun setVibrateEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_VIBRATE_ENABLED, enabled) }
        _vibrateFlow.value = enabled
    }

    fun getDarkThemeEnabled(): Boolean = _darkThemeFlow.value

    fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_THEME_ENABLED, enabled) }
        _darkThemeFlow.value = enabled
    }

    fun getSavedLanguage(): String? {
        val langCode = prefs.getString(KEY_SELECTED_LANGUAGE, null)
        Timber.tag("jejePrefs").i("getSavedLanguage() called, loaded code: '$langCode' for key '$KEY_SELECTED_LANGUAGE'")
        return langCode
    }

    private fun getSelectedLanguageFromPrefs(): String? =
        prefs.getString(KEY_SELECTED_LANGUAGE, null)

    private val _isFirstTime = MutableStateFlow(true)
    val isFirstTime: StateFlow<Boolean> = _isFirstTime

    init {
        _selectedLanguageFlow.value = prefs.getString(KEY_SELECTED_LANGUAGE, null)
        _isFirstTime.value = prefs.getBoolean(KEY_IS_FIRST_TIME, true)
    }

    /**
     * Marks that the user has completed onboarding.
     */
    fun setOnboardingCompleted() {
        prefs.edit {
            putBoolean(KEY_IS_FIRST_TIME, false)
        }
        _isFirstTime.value = false
    }
}
