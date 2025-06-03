package com.thezayin.generate.presentation.di

import com.thezayin.generate.data.repository.QrRepositoryImpl
import com.thezayin.generate.domain.repository.QrRepository
import com.thezayin.generate.domain.usecase.GenerateQrUseCase
import com.thezayin.generate.presentation.GenerateViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val generateModule = module {
    factoryOf(::QrRepositoryImpl) bind QrRepository::class
    factoryOf(::GenerateQrUseCase)
    viewModelOf(::GenerateViewModel)
}