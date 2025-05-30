package com.thezayin.scanner.presentation.scanner.analyzer

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val onBarcodeScanned: (String) -> Unit, // Callback to notify ViewModel
    private val shouldContinueScanningProvider: () -> Boolean // ViewModel dictates if continuous scanning is active
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS) // Be specific if possible
        // .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS) // Use if truly all formats are needed
        .build()
    private val scanner = BarcodeScanning.getClient(options)

    // Instance-specific flag: true after the first barcode is successfully passed to onBarcodeScanned in single-scan mode.
    // This prevents multiple callbacks for the same logical scan operation before the ViewModel stops the analyzer.
    private var singleScanCallbackFiredThisInstance = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val isInContinuousScanMode = shouldContinueScanningProvider()

        // If in single-scan mode AND this analyzer instance has already successfully called back once.
        if (!isInContinuousScanMode && singleScanCallbackFiredThisInstance) {
            imageProxy.close() // Still need to close the proxy.
            return // Prevent re-processing or re-callbacking. ViewModel should have stopped analysis.
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    barcodes.firstOrNull()?.rawValue?.let { result ->
                        if (isInContinuousScanMode) {
                            // BATCH MODE: Always callback for each new valid detection.
                            onBarcodeScanned(result)
                        } else {
                            // SINGLE SCAN MODE: Callback only once for this analyzer instance.
                            if (!singleScanCallbackFiredThisInstance) {
                                singleScanCallbackFiredThisInstance = true
                                onBarcodeScanned(result)
                                // ViewModel is now responsible for calling stopImageAnalysisSession()
                                // which will clear this analyzer from the ImageAnalysis use case.
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarcodeAnalyzer", "ML Kit Barcode Scanning Failed", e)
                // Optionally, could have a specific error callback to ViewModel.
            }
            .addOnCompleteListener {
                imageProxy.close() // Crucial: Always close ImageProxy.
            }
    }
    // No explicit reset needed IF a new BarcodeAnalyzer instance is created each time
    // `startImageAnalysisSession` is called in the ViewModel, as singleScanCallbackFiredThisInstance
    // is an instance variable.
}