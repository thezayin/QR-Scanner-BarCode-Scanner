package com.thezayin.start_up.onboarding

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.framework.ads.loader.GoogleNativeAdLoader
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.start_up.onboarding.actions.OnboardingActions
import com.thezayin.start_up.onboarding.model.OnboardingPage
import com.thezayin.start_up.onboarding.state.OnboardingState
import com.thezayin.values.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    application: Application,
    private val preferencesManager: PreferencesManager,
    val remoteConfig: RemoteConfig
) : ViewModel() {
    var nativeAd = mutableStateOf<NativeAd?>(null)
        private set

    private val _state = MutableStateFlow(
        OnboardingState(
            pages = listOf(
                OnboardingPage(
                    image = R.drawable.img_bar_code,
                    title = application.getString(R.string.barcode_scanner),
                    subtitle = application.getString(R.string.barcode_details)
                ),
                OnboardingPage(
                    image = R.drawable.img_qr_code,
                    title = application.getString(R.string.qr_code_scanner),
                    subtitle = application.getString(R.string.qr_code_details)
                ),
            )
        )
    )
    val state: StateFlow<OnboardingState> = _state

    fun onAction(action: OnboardingActions) {
        viewModelScope.launch {
            when (action) {
                is OnboardingActions.NextPage -> {
                    _state.update { currentState ->
                        val newPage =
                            (currentState.currentPage + 1).coerceAtMost(currentState.pages.size - 1)
                        currentState.copy(currentPage = newPage)
                    }
                }

                is OnboardingActions.CompleteOnboarding -> {
                    _state.update { currentState ->
                        currentState.copy(isOnboardingCompleted = true)
                    }
                    preferencesManager.setOnboardingCompleted()
                }

                is OnboardingActions.ShowError -> {
                    _state.update { currentState ->
                        currentState.copy(error = action.errorMessage)
                    }
                }
            }
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
}

