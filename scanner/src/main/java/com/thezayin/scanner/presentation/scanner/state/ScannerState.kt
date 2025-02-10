package com.thezayin.scanner.presentation.scanner.state

data class ScannerState(
    val zoomLevel: Float = 1f,
    val error: String? = null,
    val qrCodeFound: Boolean? = null,
    val scannedResult: String? = null,
    val isFlashlightOn: Boolean = false,
    val scannedImageUri: String? = null,
    val navigateToResultScreen: String? = null,
)