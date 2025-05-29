package com.thezayin.qrscanner.ui.premium.presentation.action

import com.thezayin.qrscanner.ui.premium.domain.model.SubscriptionDetails

sealed class PremiumActions {
    object ShowLoading : PremiumActions()
    object HideLoading : PremiumActions()
    data class ErrorMessage(val message: String) : PremiumActions()
    data class SubscriptionsLoaded(val subscriptions: List<SubscriptionDetails>) : PremiumActions()
    data class SubscriptionSelected(val plan: SubscriptionDetails) : PremiumActions()
    data class PurchaseSuccessful(val isPremium: Boolean) : PremiumActions()
    data class PurchaseFailed(val errorMessage: String) : PremiumActions()
}
