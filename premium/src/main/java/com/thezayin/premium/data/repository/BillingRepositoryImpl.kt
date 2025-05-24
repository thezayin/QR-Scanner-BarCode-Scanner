package com.thezayin.premium.data.repository

import android.app.Activity
import android.content.Context
import com.funsol.iap.billing.FunSolBillingHelper
import com.funsol.iap.billing.listeners.BillingEventListener
import com.funsol.iap.billing.model.ErrorType
import com.funsol.iap.billing.model.FunsolPurchase
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.utils.Response
import com.thezayin.premium.domain.repository.BillingRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class BillingRepositoryImpl(
    private val context: Context,
    preferencesManager: PreferencesManager // kept in case you need it later
) : BillingRepository {

    private companion object {
        const val PRODUCT_WEEKLY = "weekly.sub.removeads"
        const val PRODUCT_YEARLY = "yearly.sub.removeads"
        const val BASEPLAN_WEEKLY = "s1w"
        const val BASEPLAN_YEARLY = "s1y"
    }

    private val errorChannel = Channel<String>(Channel.BUFFERED)
    private val billingHelper = FunSolBillingHelper(context).apply {
        setSubProductIds(mutableListOf(BASEPLAN_WEEKLY, BASEPLAN_YEARLY))
        initialize()
    }

    private fun activity(): Activity? = context as? Activity

    override fun subscribeToWeeklyPlan(): Flow<Response<Boolean>> =
        subscribeInternal(PRODUCT_WEEKLY)

    override fun subscribeToYearlyPlan(): Flow<Response<Boolean>> =
        subscribeInternal(PRODUCT_YEARLY)

    override fun getSubscriptionPrice(subscriptionId: String): Flow<Response<Double>> =
        flow<Response<Double>> {
            val priceStr = billingHelper.getSubscriptionProductPriceById(subscriptionId)?.price
                ?: error("Price not found for $subscriptionId")
            emit(Response.Success(priceStr.toDouble()))
        }.safeCatch {
            "Failed to get subscription price: ${it.message}"
        }

    override fun checkPremiumStatus(): Flow<Response<Boolean>> =
        flow<Response<Boolean>> { emit(Response.Success(billingHelper.isPremiumUser)) }.safeCatch {
            "Failed to check premium status: ${it.message}"
        }

    override fun isUserSubscribed(): Flow<Response<Boolean>> = flow<Response<Boolean>> {
        val weekly = billingHelper.isSubsPremiumUserByBasePlanId(BASEPLAN_WEEKLY)
        val yearly = billingHelper.isSubsPremiumUserByBasePlanId(BASEPLAN_YEARLY)
        emit(Response.Success(weekly || yearly))
    }.safeCatch {
        "Subscription status check failed: ${it.message}"
    }

    private fun subscribeInternal(productId: String): Flow<Response<Boolean>> = flow {
        val act = activity() ?: return@flow emit(Response.Error("Context must be an Activity"))
        billingHelper.setBillingEventListener(listener)
        try {
            billingHelper.subscribe(act, productId)
            emit(Response.Success(true))
        } catch (t: Throwable) {
            emit(Response.Error("Subscription failed: ${t.message}"))
        }
    }.safeCatch {
        "Subscription failed: ${it.message}"
    }

    private val listener = object : BillingEventListener {
        override fun onProductsPurchased(purchases: List<FunsolPurchase?>) {
            purchases.filterNotNull().forEach {
                Timber.d("Purchase → $it")
            }
        }

        override fun onPurchaseAcknowledged(purchase: FunsolPurchase) {
            Timber.d("Acknowledged → $purchase")
        }

        override fun onPurchaseConsumed(purchase: FunsolPurchase) {
            Timber.d("Consumed → $purchase")
        }

        override fun onBillingError(error: ErrorType) {
            val msg = when (error) {
                ErrorType.CLIENT_NOT_READY -> "Client not ready"
                ErrorType.CLIENT_DISCONNECTED -> "Client disconnected"
                ErrorType.PRODUCT_NOT_EXIST -> "Product missing"
                ErrorType.BILLING_ERROR -> "Billing error"
                ErrorType.USER_CANCELED -> "User canceled"
                ErrorType.SERVICE_UNAVAILABLE -> "Service unavailable"
                else -> "Unknown billing error"
            }
            Timber.e("Billing error: $msg")
            errorChannel.trySend(msg)
        }
    }

    private fun <T> Flow<Response<T>>.safeCatch(messageBuilder: (Throwable) -> String): Flow<Response<T>> =
        catch { t ->
            if (t is CancellationException) throw t
            emit(Response.Error(messageBuilder(t)))
        }
}