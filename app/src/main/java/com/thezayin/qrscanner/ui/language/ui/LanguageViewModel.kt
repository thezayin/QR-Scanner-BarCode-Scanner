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
    private val _recreateActivityEvent = MutableSharedFlow<Unit>(replay = 0)
    val recreateActivityEvent = _recreateActivityEvent.asSharedFlow()

    fun onLanguageSelected(languageCode: String) {
        if (languageCode != _selectedLanguageCode.value) {
            try {
                localeHelper.setLocale(languageCode)
                _selectedLanguageCode.value = languageCode
                viewModelScope.launch {
                    _recreateActivityEvent.emit(Unit)
                }
                restart.restartAppToMainMenu()
            } catch (e: Exception) {
                Timber.tag(TAG).e(
                    e, "Error during language selection process for code $languageCode"
                )
            }
        } else {
            Timber.tag(TAG).d("Language $languageCode is already selected. No action taken.")
        }
    }
}