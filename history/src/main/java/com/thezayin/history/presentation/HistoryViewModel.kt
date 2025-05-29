package com.thezayin.history.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.ads.admob.domain.repository.RewardedAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.framework.session.ScanSessionManager
import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.domain.model.ScanItem
import com.thezayin.history.domain.usecase.DeleteCreateItemUseCase
import com.thezayin.history.domain.usecase.DeleteScanItemUseCase
import com.thezayin.history.domain.usecase.GetAllCreateItemsUseCase
import com.thezayin.history.domain.usecase.GetAllScanItemsUseCase
import com.thezayin.history.domain.usecase.UpdateScanFavoriteUseCase
import com.thezayin.history.presentation.event.HistoryEvent
import com.thezayin.history.presentation.state.HistoryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getAllScanItemsUseCase: GetAllScanItemsUseCase,
    private val getAllCreateItemsUseCase: GetAllCreateItemsUseCase,
    private val updateScanFavoriteUseCase: UpdateScanFavoriteUseCase,
    private val deleteScanItemUseCase: DeleteScanItemUseCase,
    private val deleteCreateItemUseCase: DeleteCreateItemUseCase,
    private val sessionManager: ScanSessionManager,
    val remoteConfig: RemoteConfig,
    val adManager: InterstitialAdManager,
    val rewardedAdManager: RewardedAdManager,
    val pref: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state

    init {
        onEvent(HistoryEvent.LoadData)
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.SelectTab -> {
                _state.value = _state.value.copy(selectedTab = event.tab)
                onEvent(HistoryEvent.LoadData)
            }

            is HistoryEvent.ToggleScanFavorite -> {
                toggleScanFavorite(event.item)
            }

            is HistoryEvent.DeleteScanItem -> {
                deleteScanItem(event.item)
            }

            is HistoryEvent.DeleteCreateItem -> {
                deleteCreateItem(event.item)
            }

            HistoryEvent.LoadData -> {
                loadData()
            }

            is HistoryEvent.OpenScanItem -> {
                openScanItem(event.item)
            }
        }
    }

    fun initManager(activity: Activity) {
        adManager.loadAd(activity)
        rewardedAdManager.loadAd(activity)
    }

    private fun openScanItem(item: ScanItem) {
        viewModelScope.launch {
            sessionManager.clearScanResults()
            sessionManager.saveScanResult(
                imageUri = item.imageUri, result = item.scannedText
            )
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                val scans = getAllScanItemsUseCase().sortedByDescending { it.timestamp }
                val creates = getAllCreateItemsUseCase().sortedByDescending { it.timestamp }
                _state.value = _state.value.copy(
                    scanItems = scans, createItems = creates, isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false, error = e.message ?: "Unknown error"
                )
            }
        }
    }

    private fun toggleScanFavorite(item: ScanItem) {
        viewModelScope.launch {
            try {
                val updated = item.copy(isFavorite = !item.isFavorite)
                updateScanFavoriteUseCase(updated)
                val newList = _state.value.scanItems.map { if (it.id == item.id) updated else it }
                _state.value = _state.value.copy(scanItems = newList)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun deleteScanItem(item: ScanItem) {
        viewModelScope.launch {
            try {
                deleteScanItemUseCase(item)
                val newList = _state.value.scanItems.filter { it.id != item.id }
                _state.value = _state.value.copy(scanItems = newList)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun deleteCreateItem(item: CreateItem) {
        viewModelScope.launch {
            try {
                deleteCreateItemUseCase(item)
                val newList = _state.value.createItems.filter { it.id != item.id }
                _state.value = _state.value.copy(createItems = newList)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}