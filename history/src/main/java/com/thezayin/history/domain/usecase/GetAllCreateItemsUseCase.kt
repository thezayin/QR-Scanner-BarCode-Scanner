package com.thezayin.history.domain.usecase

import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.domain.repository.HistoryRepository

class GetAllCreateItemsUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(): List<CreateItem> = repository.getAllCreateItems()
}