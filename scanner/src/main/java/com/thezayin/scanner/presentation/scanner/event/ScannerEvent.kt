package com.thezayin.scanner.presentation.scanner.event

sealed class ScannerEvent {
    data object ToggleFlashlight : ScannerEvent()
    data class ChangeZoom(val zoomLevel: Float) : ScannerEvent()
    data class ShowError(val message: String) : ScannerEvent()
    data class ImageSelected(val imageUri: String) : ScannerEvent()
    data class ProcessScanResult(val result: String) : ScannerEvent()
    data class ImagesSelected(val imageUris: List<String>) : ScannerEvent()
}