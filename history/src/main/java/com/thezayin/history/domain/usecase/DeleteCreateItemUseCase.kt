package com.thezayin.history.domain.usecase

import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.domain.repository.HistoryRepository

class DeleteCreateItemUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(item: CreateItem) = repository.deleteCreateItem(item)
}