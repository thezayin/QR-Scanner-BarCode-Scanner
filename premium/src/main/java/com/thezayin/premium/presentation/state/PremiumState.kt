package com.thezayin.premium.presentation.state

data class PremiumState(
    val isPremium: Boolean = false,
    val isSubscribed: Boolean = false,
    val weeklyPrice: Double? = null,
    val yearlyPrice: Double? = null,
    val selectedPlan: PlanType = PlanType.Weekly,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class PlanType { Weekly, Yearly }