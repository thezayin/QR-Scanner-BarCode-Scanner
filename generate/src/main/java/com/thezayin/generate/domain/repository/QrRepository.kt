package com.thezayin.generate.domain.repository

import android.graphics.Bitmap
import com.thezayin.generate.domain.model.QrContent

/**
 * A repository interface for generating a raw string from a QrContent model
 * that can be encoded into a QR code.
 */
interface QrRepository {
    suspend fun buildQrBitmap(content: QrContent): Bitmap
}