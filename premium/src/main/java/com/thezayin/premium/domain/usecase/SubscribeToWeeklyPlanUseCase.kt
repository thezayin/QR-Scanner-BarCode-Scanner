package com.thezayin.premium.domain.usecase

import com.thezayin.framework.utils.Response
import com.thezayin.premium.domain.repository.BillingRepository
import kotlinx.coroutines.flow.Flow

class SubscribeToWeeklyPlanUseCase(private val billingRepository: BillingRepository) {
    fun execute(): Flow<Response<Boolean>> {
        return billingRepository.subscribeToWeeklyPlan()
    }
}