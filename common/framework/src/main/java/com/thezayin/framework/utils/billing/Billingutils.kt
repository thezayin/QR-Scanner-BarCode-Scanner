package com.thezayin.framework.utils.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.funsol.iap.billing.FunSolBillingHelper
import com.funsol.iap.billing.listeners.BillingClientListener
import com.funsol.iap.billing.listeners.BillingEventListener
import com.funsol.iap.billing.model.ErrorType
import com.funsol.iap.billing.model.FunsolPurchase


var isPremium = false


fun checkIsPremium(context: Context){
   isPremium= FunSolBillingHelper(context).isPremiumUser

}


fun initializeBilling(app: Context) {
    FunSolBillingHelper(app)
        .setSubProductIds(mutableListOf("Subs Product Id", "Subs Product Id 2"))
        .setInAppProductIds(mutableListOf("In-App Product Id"))
        .initialize()
}

fun initializeClient(context: Context){
    FunSolBillingHelper(context)
        .setSubProductIds(mutableListOf("Subs Product Id", "Subs Product Id 2"))
        .setInAppProductIds(mutableListOf("In-App Product Id"))
        .enableLogging().setBillingClientListener(object : BillingClientListener {
            override fun onPurchasesUpdated() {
                Log.i("billing", "onPurchasesUpdated: called when user latest premium status fetched ")
            }

            override fun onClientReady() {
                Log.i("billing", "onClientReady: Called when client ready after fetch products details and active product against user")
            }

            override fun onClientInitError() {
                Log.i("billing", "onClientInitError: Called when client fail to init")
            }

        })
        .initialize()
}

fun buyInApp(context: Activity){
    FunSolBillingHelper(context).buyInApp(context,"In-App Product Id",false)
}

fun getAllProductPrices(context: Context){
    FunSolBillingHelper(context).getAllProductPrices()
}

fun getInAppPrice(context: Context){
    FunSolBillingHelper(context).getInAppProductPriceById("In-App Product Id")?.price
}

fun getSubscriptionPrice(context: Context){
    FunSolBillingHelper(context).getSubscriptionProductPriceById("Base Plan ID")?.price
}

fun setBillingListener(context: Context){

    FunSolBillingHelper(context).setBillingEventListener(object : BillingEventListener {


        override fun onBillingError(error: ErrorType) {
            when (error) {
                ErrorType.CLIENT_NOT_READY -> {

                }
                ErrorType.CLIENT_DISCONNECTED -> {

                }
                ErrorType.PRODUCT_NOT_EXIST -> {

                }
                ErrorType.BILLING_ERROR -> {

                }
                ErrorType.USER_CANCELED -> {

                }
                ErrorType.SERVICE_UNAVAILABLE -> {

                }
                ErrorType.BILLING_UNAVAILABLE -> {

                }
                ErrorType.ITEM_UNAVAILABLE -> {

                }
                ErrorType.DEVELOPER_ERROR -> {

                }
                ErrorType.ERROR -> {

                }
                ErrorType.ITEM_ALREADY_OWNED -> {

                }
                ErrorType.ITEM_NOT_OWNED -> {

                }

                ErrorType.SERVICE_DISCONNECTED -> {

                }

                ErrorType.ACKNOWLEDGE_ERROR -> {

                }

                ErrorType.ACKNOWLEDGE_WARNING -> {

                }

                ErrorType.OLD_PURCHASE_TOKEN_NOT_FOUND -> {

                }

                ErrorType.CONSUME_ERROR -> {

                }
                else -> {

                }
            }
        }

        override fun onProductsPurchased(purchases: List<FunsolPurchase?>) {
            TODO("Not yet implemented")
        }

        override fun onPurchaseAcknowledged(purchase: FunsolPurchase) {
            TODO("Not yet implemented")
        }

        override fun onPurchaseConsumed(purchase: FunsolPurchase) {
            TODO("Not yet implemented")
        }
    })

}