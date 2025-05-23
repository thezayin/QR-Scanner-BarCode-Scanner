package com.thezayin.premium.domain.repository

import com.thezayin.framework.utils.Response
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    fun subscribeToYearlyPlan(): Flow<Response<Boolean>>
    fun subscribeToWeeklyPlan():Flow <Response<Boolean>>
    fun getSubscriptionPrice(subscriptionId: String): Flow<Response<Double>>
    fun checkPremiumStatus(): Flow<Response<Boolean>>
    fun isUserSubscribed():Flow <Response<Boolean>>
}
