package com.thezayin.start_up.onboarding

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.preferences.PreferencesManager
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
    private val preferencesManager: PreferencesManager, // Retained preferences for completion status
) : ViewModel() {

    private val _state = MutableStateFlow(
        OnboardingState(
            pages = listOf(
                OnboardingPage(
                    gifResId = R.drawable.vid_bar,
                    title = application.getString(R.string.barcode_scanner),
                    subtitle = application.getString(R.string.barcode_details)
                ),
                OnboardingPage(
                    gifResId = R.drawable.vid_qr,
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
                    preferencesManager.setOnboardingCompleted()
                    _state.update { currentState ->
                        currentState.copy(isOnboardingCompleted = true)
                    }
                }

                is OnboardingActions.ShowError -> {
                    _state.update { currentState ->
                        currentState.copy(error = action.errorMessage)
                    }
                }
            }
        }
    }
}