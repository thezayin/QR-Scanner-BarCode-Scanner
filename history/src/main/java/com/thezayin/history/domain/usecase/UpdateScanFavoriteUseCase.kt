package com.thezayin.history.domain.usecase

import com.thezayin.history.domain.model.ScanItem
import com.thezayin.history.domain.repository.HistoryRepository

class UpdateScanFavoriteUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(item: ScanItem) {
        repository.updateScanFavorite(item)
    }
}