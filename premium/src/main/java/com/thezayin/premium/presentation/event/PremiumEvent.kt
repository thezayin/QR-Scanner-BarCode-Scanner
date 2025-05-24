package com.thezayin.premium.presentation.event

sealed class PremiumEvent {
    object LoadSubscriptionPrices : PremiumEvent()
    object CheckSubscriptionStatus : PremiumEvent()
    object LoadPremiumStatus : PremiumEvent()
    object SubscribeToWeekly : PremiumEvent()
    object SubscribeToYearly : PremiumEvent()
}