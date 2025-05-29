package com.thezayin.qrscanner.ui.premium.presentation.state

import com.thezayin.qrscanner.ui.premium.domain.model.SubscriptionDetails

data class PremiumState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val subscriptions: List<SubscriptionDetails> = emptyList(),
    val selectedPlan: SubscriptionDetails? = null,
    val isPremium: Boolean = false
)
