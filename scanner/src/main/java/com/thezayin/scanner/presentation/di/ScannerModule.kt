package com.thezayin.scanner.presentation.di

import com.thezayin.scanner.data.datasource.QrLocalDataSource
import com.thezayin.scanner.data.remote.ApiService
import com.thezayin.scanner.data.repository.ProductRepositoryImpl
import com.thezayin.scanner.data.repository.QrRepositoryImpl
import com.thezayin.scanner.domain.repository.ProductRepository
import com.thezayin.scanner.domain.repository.QrRepository
import com.thezayin.scanner.domain.usecase.AddProductToDbUseCase
import com.thezayin.scanner.domain.usecase.FetchProductDetailsUseCase
import com.thezayin.scanner.domain.usecase.ScanQrUseCase
import com.thezayin.scanner.domain.usecase.UpdateFavoriteUseCase
import com.thezayin.scanner.presentation.result.ResultScreenViewModel
import com.thezayin.scanner.presentation.scanner.ScannerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Dependency Injection Module for QR Scanner.
 *
 * This module provides:
 * - **QrLocalDataSource**: Handles local QR scanning using ML Kit.
 * - **QrRepository**: Manages data operations for QR scanning.
 * - **ScanQrUseCase**: Business logic layer for scanning QR codes.
 * - **ScannerViewModel**: ViewModel for managing UI state.
 */
val scannerModule = module {
    singleOf(::ApiService)
    singleOf(::QrLocalDataSource)
    factoryOf(::QrRepositoryImpl) bind QrRepository::class
    factoryOf(::ProductRepositoryImpl) bind ProductRepository::class
    singleOf(::AddProductToDbUseCase)
    singleOf(::UpdateFavoriteUseCase)
    singleOf(::ScanQrUseCase)
    singleOf(::FetchProductDetailsUseCase)
    viewModelOf(::ScannerViewModel)
    viewModelOf(::ResultScreenViewModel)
}