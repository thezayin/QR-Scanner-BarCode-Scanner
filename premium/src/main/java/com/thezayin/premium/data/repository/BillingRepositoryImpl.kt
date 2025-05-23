package com.thezayin.premium.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.funsol.iap.billing.FunSolBillingHelper
import com.funsol.iap.billing.listeners.BillingEventListener
import com.funsol.iap.billing.model.ErrorType
import com.funsol.iap.billing.model.FunsolPurchase
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.utils.Response
import com.thezayin.premium.domain.repository.BillingRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BillingRepositoryImpl(
    private val context: Context, private val preferencesManager: PreferencesManager
) : BillingRepository {
    val errorChannel = Channel<String>(Channel.BUFFERED)
    private val billingHelper: FunSolBillingHelper = FunSolBillingHelper(context)

    init {
        billingHelper.setSubProductIds(
            mutableListOf(
                "yearly.sub.removeads", "weekly.sub.removeads"
            )
        )
        billingHelper.initialize()
    }

    private fun getActivity(): Activity? {
        return context as? Activity
    }

    override fun subscribeToYearlyPlan(): Flow<Response<Boolean>> = flow {
        try {
            val activity = getActivity()
            if (activity != null) {
                billingHelper.setBillingEventListener(object : BillingEventListener {
                    override fun onProductsPurchased(purchases: List<FunsolPurchase?>) {
                        purchases.forEach { purchase ->
                            purchase?.let {
                                Log.d("BillingRepository", "onProductsPurchased: $purchase")
                            }
                        }
                    }

                    override fun onPurchaseAcknowledged(purchase: FunsolPurchase) {
                        Log.d("BillingRepository", "onPurchaseAcknowledged: $purchase")
                    }

                    override fun onPurchaseConsumed(purchase: FunsolPurchase) {
                        Log.d("BillingRepository", "onPurchaseConsumed: $purchase")
                    }

                    override fun onBillingError(error: ErrorType) {
                        val errorMessage = when (error) {
                            ErrorType.CLIENT_NOT_READY -> "Billing client is not ready"
                            ErrorType.CLIENT_DISCONNECTED -> "Billing client disconnected"
                            ErrorType.PRODUCT_NOT_EXIST -> "Product does not exist"
                            ErrorType.BILLING_ERROR -> "Billing error occurred"
                            ErrorType.USER_CANCELED -> "User canceled the purchase"
                            ErrorType.SERVICE_UNAVAILABLE -> "Service unavailable"
                            else -> "An unknown billing error occurred"
                        }
                        errorChannel.trySend(errorMessage).isFailure
                    }
                })

                try {
                    billingHelper.subscribe(activity, "yearly.sub.removeads")
                    emit(Response.Success(true))
                } catch (exception: Exception) {
                    emit(Response.Error("Subscription failed: ${exception.message}"))
                }
            } else {
                emit(Response.Error("Context must be an Activity"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override fun subscribeToWeeklyPlan(): Flow<Response<Boolean>> = flow {
        try {

            val activity = getActivity()
            if (activity != null) {
                billingHelper.setBillingEventListener(object : BillingEventListener {
                    override fun onProductsPurchased(purchases: List<FunsolPurchase?>) {
                        purchases.forEach { purchase ->
                            purchase?.let {
                                Log.d("BillingRepository", "onProductsPurchased: $purchase")
                            }
                        }
                    }

                    override fun onPurchaseAcknowledged(purchase: FunsolPurchase) {
                        Log.d("BillingRepository", "onPurchaseAcknowledged: $purchase")
                    }

                    override fun onPurchaseConsumed(purchase: FunsolPurchase) {
                        Log.d("BillingRepository", "onPurchaseConsumed: $purchase")
                    }

                    override fun onBillingError(error: ErrorType) {
                        val errorMessage = when (error) {
                            ErrorType.CLIENT_NOT_READY -> "Billing client is not ready"
                            ErrorType.CLIENT_DISCONNECTED -> "Billing client disconnected"
                            ErrorType.PRODUCT_NOT_EXIST -> "Product does not exist"
                            ErrorType.BILLING_ERROR -> "Billing error occurred"
                            ErrorType.USER_CANCELED -> "User canceled the purchase"
                            ErrorType.SERVICE_UNAVAILABLE -> "Service unavailable"
                            else -> "An unknown billing error occurred"
                        }

                        errorChannel.trySend(errorMessage).isFailure
                    }
                })

                try {
                    billingHelper.subscribe(activity, "weekly.sub.removeads")
                    emit(Response.Success(true))
                } catch (exception: Exception) {
                    emit(Response.Error("Subscription failed: ${exception.message}"))
                }
            } else {
                emit(Response.Error("Context must be an Activity"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override fun getSubscriptionPrice(subscriptionId: String): Flow<Response<Double>> = flow {
        try {
            val priceString = billingHelper.getSubscriptionProductPriceById(subscriptionId)?.price
            val price = priceString?.toDoubleOrNull()
            if (price != null) {
                emit(Response.Success(price))
            } else {
                emit(Response.Error("Invalid price format"))
            }
        } catch (exception: Exception) {
            emit(Response.Error("Failed to get subscription price: ${exception.message}"))
        }
    }

    override fun checkPremiumStatus(): Flow<Response<Boolean>> = flow {
        try {
            val isPremium = billingHelper.isPremiumUser
            emit(Response.Success(isPremium))
        } catch (exception: Exception) {
            emit(Response.Error("Failed to check premium status: ${exception.message}"))
        }
    }

    override fun isUserSubscribed(): Flow<Response<Boolean>> = flow {
        try {
            val yearly = billingHelper.isSubsPremiumUserByBasePlanId("yearly.sub.removeads")
            val weekly = billingHelper.isSubsPremiumUserByBasePlanId("weekly.sub.removeads")

            val isSubscribed = yearly || weekly
            emit(Response.Success(isSubscribed))
        } catch (e: Exception) {
            emit(Response.Error("Subscription status check failed: ${e.message}"))
        }
    }
}
