package com.thezayin.history.presentation.state

import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.domain.model.ScanItem

data class HistoryState(
    val selectedTab: HistoryTab = HistoryTab.SCAN,
    val scanItems: List<ScanItem> = emptyList(),
    val createItems: List<CreateItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class HistoryTab {
    SCAN, CREATED
}