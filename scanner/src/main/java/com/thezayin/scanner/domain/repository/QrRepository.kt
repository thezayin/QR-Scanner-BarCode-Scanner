package com.thezayin.scanner.domain.repository

import com.google.mlkit.vision.common.InputImage
import com.thezayin.scanner.domain.model.QrData
import com.thezayin.scanner.domain.model.Result

/**
 * Repository interface for QR code scanning.
 * This defines the contract for how QR code data is fetched.
 */
interface QrRepository {

    /**
     * Scans a given image for a QR code and returns the result.
     *
     * @param image The input image to be scanned for a QR code.
     * @return A [Result] containing either a successful scan result with [QrData]
     * or a failure with an exception.
     */
    suspend fun scanQr(image: InputImage): Result<QrData>
}
