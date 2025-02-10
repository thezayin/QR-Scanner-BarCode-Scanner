package com.thezayin.scanner.domain.model

/**
 * Data class representing the content of a scanned QR code or barcode.
 *
 * @property content The text content extracted from the scanned QR code or barcode.
 */
data class QrData(
    val content: String
)
