package com.thezayin.generate.presentation.di

import com.thezayin.generate.data.repository.QrRepositoryImpl
import com.thezayin.generate.domain.repository.QrRepository
import com.thezayin.generate.domain.usecase.GenerateQrUseCase
import com.thezayin.generate.presentation.GenerateViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val generateModule = module {
    single<QrRepository> {
        QrRepositoryImpl(get(), get())
    }
    factory {
        GenerateQrUseCase(
            repository = get()
        )
    }
    viewModelOf(::GenerateViewModel)
}