package com.thezayin.premium.presentation.di

import com.thezayin.premium.data.repository.BillingRepositoryImpl
import com.thezayin.premium.domain.repository.BillingRepository
import com.thezayin.premium.domain.usecase.CheckPremiumStatusUseCase
import com.thezayin.premium.domain.usecase.GetSubscriptionPriceUseCase
import com.thezayin.premium.domain.usecase.IsUserSubscribedUseCase
import com.thezayin.premium.domain.usecase.SubscribeToWeeklyPlanUseCase
import com.thezayin.premium.domain.usecase.SubscribeToYearlyPlanUseCase
import com.thezayin.premium.presentation.PremiumViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val billingModule = module {
    singleOf(::BillingRepositoryImpl) bind BillingRepository::class
    singleOf(::CheckPremiumStatusUseCase)
    singleOf(::IsUserSubscribedUseCase)
    singleOf(::GetSubscriptionPriceUseCase)
    singleOf(::SubscribeToWeeklyPlanUseCase)
    singleOf(::SubscribeToYearlyPlanUseCase)
    viewModelOf(::PremiumViewModel)
}