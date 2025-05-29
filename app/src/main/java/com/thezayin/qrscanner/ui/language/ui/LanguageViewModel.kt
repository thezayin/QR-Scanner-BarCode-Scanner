package com.thezayin.qrscanner.ui.language.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.qrscanner.ui.language.model.LanguageItem
import com.thezayin.qrscanner.ui.language.utils.LocaleHelper
import com.thezayin.qrscanner.utils.AppRestartManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LanguageViewModel(
    private val localeHelper: LocaleHelper,
    private val restart: AppRestartManager,
    val availableLanguages: List<LanguageItem>,
) : ViewModel() {

    private companion object {
        private const val TAG = "LanguageViewModel"
    }

    private val _selectedLanguageCode = MutableStateFlow(localeHelper.getPersistedLocaleCode())
    val selectedLanguageCode = _selectedLanguageCode.asStateFlow()

    // This event is for potential activity recreation, kept from original logic.
    private val _recreateActivityEvent = MutableSharedFlow<Unit>(replay = 0)
    val recreateActivityEvent = _recreateActivityEvent.asSharedFlow()

    // New event specifically for when the user taps the ALREADY selected language.
    private val _languageSelectionConfirmedEvent = MutableSharedFlow<Unit>(replay = 0)
    val languageSelectionConfirmedEvent = _languageSelectionConfirmedEvent.asSharedFlow()

    fun onLanguageSelected(languageCode: String) {
        if (languageCode != _selectedLanguageCode.value) {
            Timber.tag(TAG).d("New language selected: $languageCode. Current: ${_selectedLanguageCode.value}")
            try {
                localeHelper.setLocale(languageCode) // Persists and updates current config
                _selectedLanguageCode.value = languageCode // Update UI state

                // Emit recreate event as per original logic, potentially for activity.recreate()
                viewModelScope.launch {
                    _recreateActivityEvent.emit(Unit)
                }
                // Restart app to apply language changes thoroughly.
                restart.restartAppToMainMenu()
            } catch (e: Exception) {
                Timber.tag(TAG).e(
                    e, "Error during language selection process for code $languageCode"
                )
            }
        } else {
            Timber.tag(TAG).d("Language $languageCode is already selected. Emitting confirmation event.")
            viewModelScope.launch {
                _languageSelectionConfirmedEvent.emit(Unit)
            }
        }
    }
}