package com.thezayin.history.presentation.event

import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.domain.model.ScanItem
import com.thezayin.history.presentation.state.HistoryTab

sealed class HistoryEvent {
    data class SelectTab(val tab: HistoryTab) : HistoryEvent()
    data class OpenScanItem(val item: ScanItem) : HistoryEvent()
    data class ToggleScanFavorite(val item: ScanItem) : HistoryEvent()
    data class DeleteScanItem(val item: ScanItem) : HistoryEvent()
    data class DeleteCreateItem(val item: CreateItem) : HistoryEvent()
    data object LoadData : HistoryEvent()
}