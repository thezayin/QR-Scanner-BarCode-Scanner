package com.thezayin.history.presentation.di

import com.thezayin.history.data.repository.HistoryRepositoryImpl
import com.thezayin.history.domain.repository.HistoryRepository
import com.thezayin.history.domain.usecase.DeleteCreateItemUseCase
import com.thezayin.history.domain.usecase.DeleteScanItemUseCase
import com.thezayin.history.domain.usecase.GetAllCreateItemsUseCase
import com.thezayin.history.domain.usecase.GetAllScanItemsUseCase
import com.thezayin.history.domain.usecase.UpdateScanFavoriteUseCase
import com.thezayin.history.presentation.HistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val historyModule = module {
    factoryOf(::HistoryRepositoryImpl) bind HistoryRepository::class
    factoryOf(::GetAllScanItemsUseCase)
    factoryOf(::GetAllCreateItemsUseCase)
    factoryOf(::UpdateScanFavoriteUseCase)
    factoryOf(::DeleteScanItemUseCase)
    factoryOf(::DeleteCreateItemUseCase)
    viewModelOf(::HistoryViewModel)
}