package com.thezayin.generate.domain.usecase

import android.graphics.Bitmap
import com.thezayin.generate.domain.model.QrContent
import com.thezayin.generate.domain.repository.QrRepository

/**
 * Use case responsible for generating the raw string that can be
 * fed into a QR code generation library.
 */
class GenerateQrUseCase(private val repository: QrRepository) {

    suspend operator fun invoke(content: QrContent): Bitmap {
        return repository.buildQrBitmap(content)
    }
}