package com.thezayin.scanner.presentation.di

import com.thezayin.scanner.data.datasource.QrLocalDataSource
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
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModelOf
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
    // Provides an instance of QrLocalDataSource (used for QR scanning)
    single { QrLocalDataSource(get()) }

    val json = Json { ignoreUnknownKeys = true }

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    // Provides a repository implementation for managing QR scan operations
    single<QrRepository> { QrRepositoryImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl(httpClient, get()) }
    factory { AddProductToDbUseCase(get()) }
    factory { UpdateFavoriteUseCase(get()) }

    // Factory for UseCase - ensures a new instance per request
    factory { ScanQrUseCase(get()) }
    factory { FetchProductDetailsUseCase(get()) }

    // Provides a ViewModel instance to be used within the UI layer
    viewModelOf(::ScannerViewModel)
    viewModelOf(::ResultScreenViewModel)
}
