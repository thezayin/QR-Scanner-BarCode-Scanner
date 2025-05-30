package com.thezayin.qrscanner.ui.premium.presentation.handler

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.common.collect.ImmutableList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.framework.preferences.PreferencesManager
import timber.log.Timber

class PurchaseHandler(
    private val context: Context, private val prefs: PreferencesManager
) {
    private var isConnecting = false
    private val pendingOnReadyActions =
        mutableListOf<Pair<() -> Unit, ((BillingResult?) -> Unit)?>>()

    private var onPurchaseHandledListener: ((isSuccess: Boolean, message: String?) -> Unit)? = null
    fun setOnPurchaseHandledListener(listener: (isSuccess: Boolean, message: String?) -> Unit) {
        this.onPurchaseHandledListener = listener
    }

    private val purchasesUpdatedListener = PurchaseUpdateListener()
    private val client = BillingClient.newBuilder(context).setListener(purchasesUpdatedListener)
        .enablePendingPurchases().build()

    private fun ensureConnection(
        onConnected: () -> Unit, onError: ((BillingResult?) -> Unit)? = null
    ) {
        if (client.isReady) {
            Timber.tag("PurchaseHandler")
                .d("ensureConnection: Client is ready. Executing action directly.")
            try {
                onConnected()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Timber.tag("PurchaseHandler")
                    .e(e, "ensureConnection: Error executing onConnected directly.")
                onError?.invoke(
                    BillingResult.newBuilder()
                        .setResponseCode(BillingClient.BillingResponseCode.ERROR)
                        .setDebugMessage("Internal error during onConnected execution: ${e.message}")
                        .build()
                )
            }
            return
        }

        pendingOnReadyActions.add(onConnected to onError)
        Timber.tag("PurchaseHandler")
            .d("ensureConnection: Client not ready. Action queued. Queue size: ${pendingOnReadyActions.size}")

        if (!isConnecting) {
            isConnecting = true
            Timber.tag("PurchaseHandler")
                .d("ensureConnection: Starting new billing client connection.")
            client.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    isConnecting = false
                    val actionsToProcess = ArrayList(pendingOnReadyActions)
                    pendingOnReadyActions.clear()

                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Timber.tag("PurchaseHandler")
                            .d("ensureConnection: Billing setup successful. Processing ${actionsToProcess.size} pending actions.")
                        actionsToProcess.forEach { (action, specificOnErrorAction) ->
                            try {
                                action.invoke()
                            } catch (e: Exception) {
                                FirebaseCrashlytics.getInstance().recordException(e)
                                Timber.tag("PurchaseHandler").e(
                                    e,
                                    "ensureConnection: Error executing pending action after successful connection."
                                )
                                specificOnErrorAction?.invoke(
                                    BillingResult.newBuilder()
                                        .setResponseCode(BillingClient.BillingResponseCode.ERROR)
                                        .setDebugMessage("Internal error executing action: ${e.message}")
                                        .build()
                                )
                            }
                        }
                    } else {
                        Timber.tag("PurchaseHandler")
                            .e("ensureConnection: Billing setup failed: ${billingResult.debugMessage}, Response Code: ${billingResult.responseCode}")
                        actionsToProcess.forEach { (_, specificOnErrorAction) ->
                            specificOnErrorAction?.invoke(billingResult)
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {
                    isConnecting = false
                    Timber.tag("PurchaseHandler")
                        .d("ensureConnection: Billing service disconnected. Clearing ${pendingOnReadyActions.size} pending actions and notifying them.")
                    val actionsToNotifyOfDisconnect = ArrayList(pendingOnReadyActions)
                    pendingOnReadyActions.clear()
                    val disconnectResult = BillingResult.newBuilder()
                        .setResponseCode(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
                        .setDebugMessage("Billing service disconnected before action could complete.")
                        .build()
                    actionsToNotifyOfDisconnect.forEach { (_, specificOnErrorAction) ->
                        specificOnErrorAction?.invoke(disconnectResult)
                    }
                }
            })
        } else {
            Timber.tag("PurchaseHandler")
                .d("ensureConnection: Connection attempt already in progress. Action added to queue (now size: ${pendingOnReadyActions.size}).")
        }
    }

    fun queryEntitlements() {
        ensureConnection(onConnected = {
            Timber.tag("PurchaseHandler")
                .d("queryEntitlements: Connection ready. Querying purchases.")
            client.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)
                    .build()
            ) { billingResult, purchasesList ->
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    Timber.tag("PurchaseHandler")
                        .e("Error querying purchases: ${billingResult.debugMessage}, Code: ${billingResult.responseCode}")
                    prefs.updatePremiumStatus(false)
                    return@queryPurchasesAsync
                }

                var isEffectivelyPremium = false
                if (purchasesList.isNullOrEmpty()) {
                    prefs.updatePremiumStatus(false)
                } else {
                    var foundAcknowledged = false
                    for (purchase in purchasesList) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            if (purchase.isAcknowledged) {
                                Timber.tag("PurchaseHandler")
                                    .d("queryEntitlements: Found acknowledged active subscription: ${purchase.orderId}")
                                foundAcknowledged = true
                                break
                            } else {
                                Timber.tag("PurchaseHandler")
                                    .d("queryEntitlements: Found unacknowledged purchase ${purchase.orderId}. Attempting to handle.")
                                handlePurchase(purchase)
                            }
                        }
                    }
                    prefs.updatePremiumStatus(foundAcknowledged)
                    isEffectivelyPremium = foundAcknowledged
                }
                val currentPrefsValue = if (prefs.isPremiumFlow.value == isEffectivelyPremium) {
                    "${prefs.isPremiumFlow.value}"
                } else {
                    "${prefs.isPremiumFlow.value} (Note: Mismatch with isEffectivelyPremium: $isEffectivelyPremium)"
                }
                Timber.tag("PurchaseHandler")
                    .d("Query entitlements complete. Effective premium status from this query: $isEffectivelyPremium. Prefs flow status: $currentPrefsValue")
            }
        }, onError = { billingResult ->
            Timber.tag("PurchaseHandler")
                .e("queryEntitlements: Connection failed for querying entitlements. Error: ${billingResult?.debugMessage}")
            prefs.updatePremiumStatus(false)
        })
    }

    fun querySubscriptionProductDetails(
        productIds: List<String>,
        onDetailsFetched: (List<ProductDetails>) -> Unit,
        onError: (String) -> Unit
    ) {
        ensureConnection(onConnected = {
            Timber.tag("PurchaseHandler_QSPD")
                .d("Connection ready. Querying for product IDs: $productIds")
            val productList = productIds.map { productId ->
                QueryProductDetailsParams.Product.newBuilder().setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS).build()
            }
            val queryParams = QueryProductDetailsParams.newBuilder()
                .setProductList(ImmutableList.copyOf(productList)).build()

            client.queryProductDetailsAsync(queryParams) { billingResult, productDetailsList ->
                Timber.tag("PurchaseHandler_QSPD")
                    .d("queryProductDetailsAsync returned. Result: ${billingResult.responseCode} - ${billingResult.debugMessage}, List size: ${productDetailsList?.size ?: "null"}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (productDetailsList != null) {
                        onDetailsFetched(productDetailsList)
                    } else {
                        Timber.tag("PurchaseHandler_QSPD")
                            .w("queryProductDetailsAsync returned OK but productDetailsList is null. Reporting empty.")
                        onDetailsFetched(emptyList())
                    }
                } else {
                    Timber.tag("PurchaseHandler_QSPD")
                        .e("Error fetching product details: ${billingResult.debugMessage}")
                    onError("Failed to load subscription offers: ${billingResult.debugMessage}")
                }
            }
        }, onError = { billingResult ->
            Timber.tag("PurchaseHandler_QSPD")
                .e("Connection failed for querying subscription details. Error: ${billingResult?.debugMessage}")
            onError("Billing connection failed: ${billingResult?.debugMessage ?: "Unknown error"}")
        })
    }


    fun launchPurchaseFlow(
        activity: Activity, productId: String
    ) {
        Timber.tag("PurchaseHandler_LPF")
            .d("Attempting for productId: $productId. Ensuring connection.")
        ensureConnection(onConnected = {
            Timber.tag("PurchaseHandler_LPF")
                .d("LPF: Connection ready for $productId. Querying its details.")
            val productList = ImmutableList.of(
                QueryProductDetailsParams.Product.newBuilder().setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS).build()
            )
            val queryParams =
                QueryProductDetailsParams.newBuilder().setProductList(productList).build()

            Timber.tag("PurchaseHandler_LPF")
                .d("LPF: Calling queryProductDetailsAsync for $productId.")
            client.queryProductDetailsAsync(queryParams) { result, productDetailsList ->
                Timber.tag("PurchaseHandler_LPF")
                    .d("LPF: queryProductDetailsAsync for $productId returned. Result: ${result.responseCode} - ${result.debugMessage}, List size: ${productDetailsList?.size ?: "null"}")

                if (result.responseCode == BillingClient.BillingResponseCode.OK && !productDetailsList.isNullOrEmpty()) {
                    val productDetails = productDetailsList.first()
                    Timber.tag("PurchaseHandler_LPF")
                        .d("LPF: Product details found for $productId: ${productDetails.name}. Preparing flow params.")

                    val offerToken =
                        productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken

                    if (productDetails.productType == BillingClient.ProductType.SUBS && offerToken.isNullOrEmpty()) {
                        Timber.tag("PurchaseHandler_LPF")
                            .e("LPF: CRITICAL - Product $productId is a SUBS but NO offerToken found in its details. Purchase will likely fail. Check Play Console setup for offers (base plans).")
                        onPurchaseHandledListener?.invoke(
                            false,
                            "Subscription offer details missing for $productId. Please try again later or contact support."
                        )
                        return@queryProductDetailsAsync
                    } else if (productDetails.productType == BillingClient.ProductType.SUBS) {
                        Timber.tag("PurchaseHandler_LPF")
                            .d("LPF: Using offerToken '$offerToken' for subscription $productId.")
                    }


                    val productDetailsParamsBuilder =
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)

                    if (productDetails.productType == BillingClient.ProductType.SUBS && !offerToken.isNullOrEmpty()) {
                        productDetailsParamsBuilder.setOfferToken(offerToken)
                    }

                    val productDetailsParams = productDetailsParamsBuilder.build()

                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(ImmutableList.of(productDetailsParams)).build()

                    Timber.tag("PurchaseHandler_LPF")
                        .d("LPF: Launching billing flow for $productId with Activity: ${activity.javaClass.simpleName}")
                    val launchResult = client.launchBillingFlow(activity, billingFlowParams)
                    Timber.tag("PurchaseHandler_LPF")
                        .d("LPF: launchBillingFlow for $productId result: ${launchResult.responseCode} - ${launchResult.debugMessage}")

                    if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        Timber.tag("PurchaseHandler_LPF")
                            .e("LPF: Failed to launch billing flow for $productId: ${launchResult.debugMessage} (Code: ${launchResult.responseCode})")
                        onPurchaseHandledListener?.invoke(
                            false,
                            "Failed to initiate purchase for ${productDetails.name}: ${launchResult.debugMessage}"
                        )
                    } else {
                        Timber.tag("PurchaseHandler_LPF")
                            .d("LPF: Billing flow launched successfully for $productId. Waiting for onPurchasesUpdated.")
                    }
                } else {
                    Timber.tag("PurchaseHandler_LPF")
                        .e("LPF: Product details not found or error for $productId: Result code ${result.responseCode}, Debug: ${result.debugMessage}, List empty/null: ${productDetailsList.isNullOrEmpty()}")
                    onPurchaseHandledListener?.invoke(
                        false, "Details for ${productId} not found: ${result.debugMessage}"
                    )
                }
            }
        }, onError = { billingResult ->
            Timber.tag("PurchaseHandler_LPF")
                .e("LPF: Connection to Billing Service failed BEFORE launching purchase for $productId: ${billingResult?.debugMessage}")
            onPurchaseHandledListener?.invoke(
                false,
                "Billing connection error before purchase: ${billingResult?.debugMessage ?: "Unknown error"}"
            )
        })
    }

    private fun handlePurchase(purchase: Purchase) {
        Timber.tag("PurchaseHandler_HP")
            .d("Handling purchase for order ${purchase.orderId}, product: ${purchase.products.firstOrNull()}, state: ${purchase.purchaseState}, acknowledged: ${purchase.isAcknowledged}")
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                Timber.tag("PurchaseHandler_HP")
                    .d("Purchase ${purchase.orderId} is PURCHASED but NOT acknowledged. Attempting acknowledge.")
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                        .build()
                ensureConnection(onConnected = {
                    Timber.tag("PurchaseHandler_HP")
                        .d("HP: Connection ready for acknowledging ${purchase.orderId}.")
                    client.acknowledgePurchase(
                        acknowledgePurchaseParams, AcknowledgeListener()
                    )
                }, onError = { billingResult ->
                    Timber.tag("PurchaseHandler_HP")
                        .e("HP: Connection failed for acknowledging purchase: ${purchase.orderId}. Error: ${billingResult?.debugMessage}")
                    onPurchaseHandledListener?.invoke(
                        false,
                        "Purchase successful, but acknowledgement connection failed for ${purchase.products.firstOrNull()}. Please check entitlements or retry."
                    )
                })
            } else {
                Timber.tag("PurchaseHandler_HP")
                    .d("Purchase ${purchase.orderId} is ALREADY acknowledged and PURCHASED.")
                prefs.updatePremiumStatus(true)
                onPurchaseHandledListener?.invoke(true, null)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            Timber.tag("PurchaseHandler_HP").d("Purchase ${purchase.orderId} is PENDING.")
            prefs.updatePremiumStatus(false)
            onPurchaseHandledListener?.invoke(
                false,
                "Your purchase for ${purchase.products.firstOrNull()} is pending. We'll update your status once confirmed."
            )
        } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            Timber.tag("PurchaseHandler_HP")
                .w("Purchase ${purchase.orderId} is in UNSPECIFIED_STATE.")
            prefs.updatePremiumStatus(false)
            onPurchaseHandledListener?.invoke(
                false,
                "Purchase status for ${purchase.products.firstOrNull()} is unclear. Please check your subscriptions."
            )
        } else {
            Timber.tag("PurchaseHandler_HP")
                .w("Purchase ${purchase.orderId} in unexpected state: ${purchase.purchaseState}")
        }
    }

    inner class AcknowledgeListener : AcknowledgePurchaseResponseListener {
        override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
            Timber.tag("PurchaseHandler_AL")
                .d("Acknowledge response. Code: ${billingResult.responseCode}, Msg: ${billingResult.debugMessage}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                prefs.updatePremiumStatus(true)
                Timber.tag("PurchaseHandler_AL").d("Purchase acknowledged successfully.")
                onPurchaseHandledListener?.invoke(true, null)
            } else {
                Timber.tag("PurchaseHandler_AL")
                    .e("Acknowledgement failed. Code: ${billingResult.responseCode}, Msg: ${billingResult.debugMessage}")
                onPurchaseHandledListener?.invoke(
                    false,
                    "Acknowledgement failed: ${billingResult.debugMessage}. Premium not active. If payment was made, contact support."
                )
            }
        }
    }

    inner class PurchaseUpdateListener : PurchasesUpdatedListener {
        override fun onPurchasesUpdated(
            billingResult: BillingResult, purchases: MutableList<Purchase>?
        ) {
            Timber.tag("PurchaseHandler_PUL")
                .d("onPurchasesUpdated - Code: ${billingResult.responseCode}, Message: ${billingResult.debugMessage}, Purchases: ${purchases?.joinToString { it.orderId ?: "N/A" } ?: "null"}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                Timber.tag("PurchaseHandler_PUL").d("User cancelled the purchase flow.")
                onPurchaseHandledListener?.invoke(false, "Purchase cancelled.")
            } else {
                Timber.tag("PurchaseHandler_PUL")
                    .e("Purchase Update Error. Code: ${billingResult.responseCode}, Msg: ${billingResult.debugMessage}")
                prefs.updatePremiumStatus(false)
                var userMessage = "Purchase failed: ${billingResult.debugMessage}"
                if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    userMessage = "You already own this item. Checking your entitlements..."
                    queryEntitlements()
                }
                onPurchaseHandledListener?.invoke(false, userMessage)
            }
        }
    }

    fun endConnection() {
        Timber.tag("PurchaseHandler").d("endConnection called. Client ready: ${client.isReady}")
        if (client.isReady) {
            client.endConnection()
            Timber.tag("PurchaseHandler").d("Billing client connection ended.")
        }
        isConnecting = false
        pendingOnReadyActions.clear()
    }
}