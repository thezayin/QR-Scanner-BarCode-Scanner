package com.thezayin.history.domain.usecase

import com.thezayin.history.domain.model.ScanItem
import com.thezayin.history.domain.repository.HistoryRepository

class GetAllScanItemsUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(): List<ScanItem> {
        return repository.getAllScanItems()
    }
}