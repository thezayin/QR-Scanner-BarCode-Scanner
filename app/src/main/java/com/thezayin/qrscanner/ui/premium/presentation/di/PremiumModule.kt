package com.thezayin.qrscanner.ui.premium.presentation.di

import com.thezayin.qrscanner.ui.premium.presentation.PremiumViewModel
import com.thezayin.qrscanner.ui.premium.presentation.handler.PremiumStatusInitializer
import com.thezayin.qrscanner.ui.premium.presentation.handler.PurchaseHandler
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val premiumModule = module {
    viewModelOf(::PremiumViewModel)
    singleOf(::PurchaseHandler)
    singleOf(::PremiumStatusInitializer)
}