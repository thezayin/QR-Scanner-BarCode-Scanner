package com.thezayin.premium.presentation.state

data class PremiumState(
    val isPremium: Boolean = false,
    val isSubscribed: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val yearlySubscriptionPrice: Double? = null,
    val weeklySubscriptionPrice: Double? = null,
    val packageName: String? = null
)
