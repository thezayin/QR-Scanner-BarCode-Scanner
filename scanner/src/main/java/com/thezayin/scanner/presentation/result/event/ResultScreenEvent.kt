package com.thezayin.scanner.presentation.result.event

import android.content.Context
import com.thezayin.scanner.domain.model.ResultScreenItem

sealed class ResultScreenEvent {
    data class OpenItem(val item: ResultScreenItem, val context: Context) : ResultScreenEvent()
    data class ShareItem(val item: ResultScreenItem) : ResultScreenEvent()
    data class CopyItem(val item: ResultScreenItem) : ResultScreenEvent()
    data object Refresh : ResultScreenEvent()
    data class ToggleFavorite(val item: ResultScreenItem) : ResultScreenEvent()
    data class ConnectWifi(val item: ResultScreenItem) : ResultScreenEvent()
    data class CopyPassword(val item: ResultScreenItem, val password: String) : ResultScreenEvent()
    data class ShareWifi(val item: ResultScreenItem, val ssid: String, val password: String) :
        ResultScreenEvent()

    data class CopyWifi(val item: ResultScreenItem, val ssid: String, val password: String) :
        ResultScreenEvent()
}