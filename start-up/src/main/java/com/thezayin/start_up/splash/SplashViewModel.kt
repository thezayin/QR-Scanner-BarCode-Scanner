package com.thezayin.start_up.splash

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.ads.admob.domain.repository.AppOpenAdManager
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.start_up.splash.event.SplashEvent
import com.thezayin.start_up.splash.state.SplashState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    application: Application,
    private val preferencesManager: PreferencesManager,
    val remoteConfig: RemoteConfig,
    val appOpenAdManager: AppOpenAdManager,
    val interstitialAdManager: InterstitialAdManager
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        // Set isFirstTime from Preferences
        _state.value = _state.value.copy(
            isFirstTime = preferencesManager.isFirstTime.value
        )
        // Start the splash flow
        sendEvent(SplashEvent.LoadSplash)
    }

    fun initManagers(activity: Activity) {
        appOpenAdManager.loadAd(activity)
        interstitialAdManager.loadAd(activity)
    }

    private fun sendEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.LoadSplash -> handleLoadSplash()
            SplashEvent.NavigateNext -> handleNavigateNext()
        }
    }

    private fun handleLoadSplash() {
        viewModelScope.launch {
            // Cycle through splashTexts over 5 seconds
            val totalTime = 5000L
            val interval = totalTime / _state.value.splashTexts.size

            for (i in _state.value.splashTexts.indices) {
                _state.update {
                    it.copy(
                        currentSplashText = it.splashTexts[i],
                        currentSplashIndex = i
                    )
                }
                delay(interval)
            }
            handleNavigateNext()
        }
    }


    private fun handleNavigateNext() {
        // Once everything is ready, set navigateToNextScreen = true
        _state.update { it.copy(navigateToNextScreen = true) }
    }
}
