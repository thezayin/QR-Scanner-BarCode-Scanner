package com.thezayin.start_up.languages

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.framework.ads.loader.GoogleNativeAdLoader
import com.thezayin.framework.remote.RemoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val languageManager: LanguageManager,
    private val remoteConfig: RemoteConfig
) : ViewModel() {

    private val _state = MutableStateFlow<LanguageState>(LanguageState.Initialization)
    val state: StateFlow<LanguageState> = _state

    var nativeAd = mutableStateOf<NativeAd?>(null)
        private set

    init {
        initializeLanguages()
        observeLanguageUpdates()
    }

    private fun observeLanguageUpdates() {
        viewModelScope.launch {
            languageManager.get().collect { language ->
                val selectedLocale = java.util.Locale.forLanguageTag(language.locale.value)
                _state.value = LanguageState.Content(
                    selected = selectedLocale,
                    languages = languageManager.languages,
                    selectedLang = language
                )
            }
        }
    }

    fun initializeLanguages() {
        languageManager.init { language ->
            val selectedLocale = java.util.Locale.forLanguageTag(language.locale.value)
            _state.value = LanguageState.Content(
                selected = selectedLocale,
                languages = languageManager.languages,
                selectedLang = language
            )
        }
    }

    fun getNativeAd(context: Context) = viewModelScope.launch {
        if (remoteConfig.adConfigs.bottomAdAtOnboarding) {
            GoogleNativeAdLoader.loadNativeAd(
                context = context,
                adUnitId = remoteConfig.adUnits.nativeAd,
                onNativeAdLoaded = {
                    nativeAd.value = it
                }
            )
        }
    }

    fun onLanguageSelected(language: Language) {
        viewModelScope.launch {
            languageManager.update(language)
        }
    }
}
