package com.thezayin.scanner.data.datasource

import android.content.Context
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.thezayin.scanner.domain.model.QrData
import com.thezayin.scanner.domain.model.Result
import com.thezayin.values.R
import kotlinx.coroutines.tasks.await

/**
 * Data source responsible for handling QR code and barcode scanning.
 */
class QrLocalDataSource(private val context: Context) {
    private val scanner: BarcodeScanner

    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_CODE_128)
            .build()
        scanner = BarcodeScanning.getClient(options)
    }

    /**
     * Scans the given input image for QR codes or barcodes.
     *
     * @param image The image to scan.
     * @return A [Result] containing either the scanned data ([QrData]) or an error.
     */
    suspend fun scanImage(image: InputImage): Result<QrData> {
        return try {
            val barcodes = scanner.process(image).await()
            if (barcodes.isNotEmpty()) {
                val qrContent = barcodes.firstOrNull()?.rawValue
                if (qrContent != null) {
                    Result.Success(QrData(content = qrContent))
                } else {
                    Result.Failure(Exception(context.getString(R.string.qr_code_empty_content)))
                }
            } else {
                Result.Failure(Exception(context.getString(R.string.qr_code_not_found)))
            }
        } catch (e: Exception) {
            Result.Failure(Exception(context.getString(R.string.qr_code_scan_error), e))
        }
    }
}