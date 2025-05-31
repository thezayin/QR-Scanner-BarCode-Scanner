package com.thezayin.scanner.presentation.scanner.state

data class ScannerState(
    val isCameraReady: Boolean = false,
    val zoomLevel: Float = 1f, // Represents the currently set zoom or desired zoom
    val minZoomRatio: Float = 1f, // New: Camera's minimum zoom ratio
    val maxZoomRatio: Float = 1f, // New: Camera's maximum zoom ratio
    val error: String? = null,
    val isFlashlightOn: Boolean = false,
    val isBatchModeActive: Boolean = false,
    val batchScannedCodes: Set<String> = emptySet()
)