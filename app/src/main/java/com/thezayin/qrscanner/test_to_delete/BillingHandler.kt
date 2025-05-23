package com.thezayin.qrscanner.test_to_delete

import android.app.Activity
import com.android.billingclient.api.*
import timber.log.Timber

class BillingHandler(private val activity: Activity) {

    private val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(object : PurchasesUpdatedListener {
            override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
                Timber.d("PurchasesUpdatedListener called, result: ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        Timber.d("Purchase completed: SKU = ${purchase}, Status = ${purchase.purchaseState}")
                        // Handle purchase completion (e.g., deliver the product)
                    }
                }
            }
        })
        .build()

    // Method to initiate connection to Google Play Billing service
    fun initBillingClient() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Google Play Billing setup successful.")
                    // Now that BillingClient is connected, query for test subscriptions
                    queryTestSubscriptions()
                } else {
                    Timber.e("Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Timber.e("Billing service disconnected.")
            }
        })
    }

    // Method to query and handle test subscriptions
    private fun queryTestSubscriptions() {
        val skuList = listOf(
            "android.test.subscription",  // Test subscription ID for Google Play
            "android.test.canceled",       // Simulate canceled subscription
            "android.test.refunded",       // Simulate refunded subscription
            "android.test.item_unavailable"  // Simulate unavailable item
        )

        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)  // Add the test subscription IDs here
            .setType(BillingClient.SkuType.SUBS)  // Use BillingClient.SkuType.SUBS for subscriptions
            .build()

        // Query the subscriptions using the BillingClient
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                skuDetailsList?.forEach { skuDetails ->
                    // Log the details of the available subscriptions
                    Timber.d("Test Subscription Available: SKU = ${skuDetails.sku}, Title = ${skuDetails.title}, Price = ${skuDetails.price}")
                }
            } else {
                Timber.e("Failed to query subscriptions: ${billingResult.debugMessage}")
            }
        }
    }

    // Method to close the billing client
    fun closeBillingClient() {
        billingClient.endConnection()
    }
}
