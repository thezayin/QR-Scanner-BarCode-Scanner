package com.thezayin.scanner.presentation.scanner.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import timber.log.Timber

class BarcodeAnalyzer(
    private val onBarcodeScanned: (String) -> Unit,
    private val shouldContinueScanningProvider: () -> Boolean
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()
    private val scanner = BarcodeScanning.getClient(options)
    private var singleScanCallbackFiredThisInstance = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val isInContinuousScanMode = shouldContinueScanningProvider()
        if (!isInContinuousScanMode && singleScanCallbackFiredThisInstance) {
            imageProxy.close()
            return
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
                            onBarcodeScanned(result)
                        } else {
                            if (!singleScanCallbackFiredThisInstance) {
                                singleScanCallbackFiredThisInstance = true
                                onBarcodeScanned(result)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Timber.tag("BarcodeAnalyzer").e(e, "ML Kit Barcode Scanning Failed")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}