package com.thezayin.qrscanner.ui.premium.presentation.action

import com.thezayin.qrscanner.ui.premium.domain.model.SubscriptionDetails

sealed class PremiumActions {
    data class SubscriptionSelected(val plan: SubscriptionDetails) : PremiumActions()
}
