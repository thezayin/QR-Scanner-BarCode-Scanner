package com.thezayin.qrscanner.ui.premium.domain.model

import com.android.billingclient.api.ProductDetails

data class SubscriptionDetails(
    val id: String,
    val title: String,
    val description: String?,
    val price: String,
    val currencyCode: String,
    val type: String
)


fun ProductDetails.toSubscriptionDetails(): SubscriptionDetails? {
    val offerDetails =
        this.subscriptionOfferDetails?.firstOrNull()
    val pricingPhase =
        offerDetails?.pricingPhases?.pricingPhaseList?.firstOrNull()

    if (offerDetails == null || pricingPhase == null) {
        return null
    }

    return SubscriptionDetails(
        id = this.productId,
        title = this.name,
        description = this.description,
        price = pricingPhase.formattedPrice,
        currencyCode = pricingPhase.priceCurrencyCode,
        type = this.productType
    )
}