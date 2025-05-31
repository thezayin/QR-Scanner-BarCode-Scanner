// com.thezayin.qrscanner.ui.language.ui.LanguageViewModel.kt
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

class LanguageViewModel(
    private val localeHelper: LocaleHelper,
    private val restart: AppRestartManager,
    val availableLanguages: List<LanguageItem>,
) : ViewModel() {

    private val _selectedLanguageCode = MutableStateFlow(localeHelper.getPersistedLocaleCode())
    val selectedLanguageCode = _selectedLanguageCode.asStateFlow()
    private val _languageSelectionConfirmedEvent = MutableSharedFlow<Unit>(replay = 0)
    val languageSelectionConfirmedEvent = _languageSelectionConfirmedEvent.asSharedFlow()

    fun onLanguageSelected(languageCode: String) {
        if (languageCode != _selectedLanguageCode.value) {
            localeHelper.setLocale(languageCode)
            _selectedLanguageCode.value = languageCode
            restart.restartAppToMainMenu()
        } else {
            viewModelScope.launch {
                _languageSelectionConfirmedEvent.emit(Unit)
            }
        }
    }
}