package com.thezayin.history.domain.repository

import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.domain.model.ScanItem

interface HistoryRepository {
    suspend fun getAllScanItems(): List<ScanItem>
    suspend fun updateScanFavorite(item: ScanItem)
    suspend fun deleteScanItem(item: ScanItem)
    suspend fun getAllCreateItems(): List<CreateItem>
    suspend fun deleteCreateItem(item: CreateItem)
}