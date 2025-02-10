package com.thezayin.scanner.domain.usecase

import com.google.mlkit.vision.common.InputImage
import com.thezayin.scanner.domain.model.QrData
import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.repository.QrRepository

/**
 * Use case for scanning a QR code from an image.
 *
 * This class is responsible for executing the scanning logic
 * by delegating the request to the repository.
 */
class ScanQrUseCase(private val repository: QrRepository) {

    /**
     * Executes the QR scanning operation.
     *
     * @param image The input image to be scanned.
     * @return A [Result] object containing either the successfully scanned QR data
     * or an error if the scan fails.
     */
    suspend fun execute(image: InputImage): Result<QrData> {
        return repository.scanQr(image)
    }
}