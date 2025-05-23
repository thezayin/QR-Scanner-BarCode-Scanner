package com.thezayin.premium.presentation.event

sealed class PremiumEvent {
    object LoadPremiumStatus : PremiumEvent()
    object SubscribeToYearly : PremiumEvent()
    object SubscribeToWeekly : PremiumEvent()
    object CheckSubscriptionStatus : PremiumEvent()
    object LoadSubscriptionPrices : PremiumEvent()
    data class SetPackageName(val packageName: String) : PremiumEvent()
}
