package com.thezayin.scanner.presentation.scanner.state

data class ScannerState(
    val isCameraReady: Boolean = false,
    val zoomLevel: Float = 1f,
    val error: String? = null,
    val isFlashlightOn: Boolean = false,
    val isBatchModeActive: Boolean = false,
    val batchScannedCodes: Set<String> = emptySet()
)