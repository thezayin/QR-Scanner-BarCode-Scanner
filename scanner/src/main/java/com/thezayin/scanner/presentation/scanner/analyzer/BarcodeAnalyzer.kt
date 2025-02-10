package com.thezayin.scanner.presentation.scanner.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**
 * Analyzes camera frames for QR and barcode detection.
 *
 * This class processes images in real-time and detects QR codes or barcodes
 * using Google's ML Kit Barcode Scanner.
 *
 * @param onQrCodeScanned Callback function triggered when a QR code is detected.
 * @param onStopScanning Callback function to stop scanning after a successful detection.
 */
class BarcodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit,
    private val onStopScanning: () -> Unit
) : ImageAnalysis.Analyzer {

    // Configure the barcode scanner to detect all barcode formats.
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    // Prevents continuous scanning after a successful detection.
    private var isScanning = true

    /**
     * Processes each camera frame to detect QR codes or barcodes.
     *
     * @param imageProxy The image captured from the camera.
     */
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // If scanning is stopped, release the image and return.
        if (!isScanning) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        // Convert the captured frame to an ML Kit-compatible format.
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        // Process the image using ML Kit's barcode scanner.
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val result = barcodes.firstOrNull()?.rawValue
                if (result != null) {
                    isScanning = false // Stop further scanning after success.
                    onQrCodeScanned(result)
                    onStopScanning() // Notify the CameraPreview to unbind.
                }
            }
            .addOnCompleteListener {
                // Release the image so the next frame can be processed.
                imageProxy.close()
            }
    }
}
