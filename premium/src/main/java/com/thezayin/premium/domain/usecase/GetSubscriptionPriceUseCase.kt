package com.thezayin.premium.domain.usecase

import com.thezayin.framework.utils.Response
import com.thezayin.premium.domain.repository.BillingRepository
import kotlinx.coroutines.flow.Flow

class GetSubscriptionPriceUseCase(private val billingRepository: BillingRepository) {
    fun execute(subscriptionId: String): Flow<Response<Double>> {
        return billingRepository.getSubscriptionPrice(subscriptionId)
    }
}
