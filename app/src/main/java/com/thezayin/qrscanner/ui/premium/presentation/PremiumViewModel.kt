package com.thezayin.qrscanner.ui.premium.presentation

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.qrscanner.ui.premium.domain.model.toSubscriptionDetails
import com.thezayin.qrscanner.ui.premium.presentation.action.PremiumActions
import com.thezayin.qrscanner.ui.premium.presentation.handler.PurchaseHandler
import com.thezayin.qrscanner.ui.premium.presentation.state.PremiumState
import com.thezayin.qrscanner.utils.AppRestartManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class PremiumViewModel(
    application: Application,
    private val appRestartManager: AppRestartManager,
    private val prefs: PreferencesManager,
    private val purchaseHandler: PurchaseHandler
) : ViewModel() {

    private val _premiumState = MutableStateFlow(PremiumState())
    val premiumState: StateFlow<PremiumState> = _premiumState.asStateFlow()

    private val productIds = listOf("weekly.sub.removeads", "yearly.sub.removeads")

    init {
        Timber.d("PremiumViewModel initialized")
        _premiumState.update { it.copy(isLoading = true) }

        purchaseHandler.setOnPurchaseHandledListener { isSuccess, message ->
            viewModelScope.launch {
                Timber.d("onPurchaseHandledListener: isSuccess=$isSuccess, message=$message")
                if (isSuccess) {
                    _premiumState.update { state ->
                        state.copy(
                            isLoading = false,
                            isPremium = true,
                            errorMessage = "",
                            selectedPlan = null
                        )
                    }
                    appRestartManager.restartAppToMainMenu()
                } else {
                    _premiumState.update { state ->
                        state.copy(
                            isLoading = false,
                            isPremium = prefs.isPremiumFlow.value,
                            errorMessage = message ?: "An unknown error occurred during purchase."
                        )
                    }
                }
            }
        }
        checkCurrentUserStatusAndLoadOffers()
    }

    private fun checkCurrentUserStatusAndLoadOffers() {
        viewModelScope.launch {
            _premiumState.update { it.copy(isLoading = true) }
            purchaseHandler.queryEntitlements()
            val currentPremiumStatus = prefs.isPremiumFlow.value
            Timber.d("Initial premium status from prefs: $currentPremiumStatus")
            _premiumState.update { it.copy(isPremium = currentPremiumStatus) }
            loadSubscriptionOffers()
        }
    }


    private fun loadSubscriptionOffers() {
        _premiumState.update { it.copy(isLoading = true) }
        Timber.d("Loading subscription offers for IDs: $productIds")
        purchaseHandler.querySubscriptionProductDetails(
            productIds = productIds,
            onDetailsFetched = { productDetailsList ->
                viewModelScope.launch {
                    Timber.d("Fetched ${productDetailsList.size} product details.")
                    val subscriptions = productDetailsList.mapNotNull { it.toSubscriptionDetails() }
                    _premiumState.update {
                        it.copy(
                            isLoading = false,
                            subscriptions = subscriptions,
                            errorMessage = if (subscriptions.isEmpty() && productIds.isNotEmpty()) "No subscription offers found." else ""
                        )
                    }
                }
            },
            onError = { errorMsg ->
                viewModelScope.launch {
                    Timber.e("Error loading subscription offers: $errorMsg")
                    _premiumState.update {
                        it.copy(
                            isLoading = false, errorMessage = errorMsg
                        )
                    }
                }
            })
    }

    fun onAction(action: PremiumActions) {
        viewModelScope.launch {
            when (action) {
                is PremiumActions.SubscriptionSelected -> {
                    _premiumState.update { it.copy(selectedPlan = action.plan) }
                }
                // Other actions can be handled here if needed
                // For purchase, the screen will call purchaseSelectedPlan directly
                else -> {
                    // e.g. PremiumActions.ShowLoading, PremiumActions.HideLoading can be handled by state updates directly
                }
            }
        }
    }

    fun purchaseSelectedPlan(activity: Activity) {
        val planToPurchase = _premiumState.value.selectedPlan
        if (planToPurchase == null) {
            _premiumState.update { it.copy(errorMessage = "Please select a plan to purchase.") }
            return
        }
        Timber.d("Attempting to purchase plan: ${planToPurchase.id}")
        _premiumState.update { it.copy(isLoading = true, errorMessage = "") }
        purchaseHandler.launchPurchaseFlow(activity, planToPurchase.id)
    }

    override fun onCleared() {
        super.onCleared()
        purchaseHandler.endConnection()
        Timber.d("PremiumViewModel cleared, billing connection ended.")
    }
}