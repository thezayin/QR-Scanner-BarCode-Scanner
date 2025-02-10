package com.thezayin.scanner.data.repository

import com.google.mlkit.vision.common.InputImage
import com.thezayin.scanner.data.datasource.QrLocalDataSource
import com.thezayin.scanner.domain.model.QrData
import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.repository.QrRepository

/**
 * Implementation of [QrRepository] responsible for QR code scanning.
 * Delegates the QR code scanning operation to a local data source.
 */
class QrRepositoryImpl(
    private val localDataSource: QrLocalDataSource
) : QrRepository {

    /**
     * Scans a QR code or barcode from the given input image.
     *
     * @param image The [InputImage] to scan for QR code or barcode.
     * @return A [Result] containing either the scanned data ([QrData]) or an error.
     */
    override suspend fun scanQr(image: InputImage): Result<QrData> {
        return localDataSource.scanImage(image)
    }
}
