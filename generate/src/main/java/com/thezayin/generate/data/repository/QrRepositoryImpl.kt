package com.thezayin.generate.data.repository

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.thezayin.databases.dao.QrItemDao
import com.thezayin.databases.entity.QrItemEntity
import com.thezayin.generate.domain.model.QrContent
import com.thezayin.generate.domain.repository.QrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class QrRepositoryImpl(
    private val qrItemDao: QrItemDao, private val application: Application
) : QrRepository {

    private data class EncodeParams(
        val rawString: String, val format: BarcodeFormat, val width: Int, val height: Int
    )

    override suspend fun buildQrBitmap(content: QrContent): Bitmap =
        withContext(Dispatchers.Default) {
            val params = resolveFormatAndString(content)
            Timber.tag("QRCodeGeneration").d("Param: $params")
            val writer = MultiFormatWriter()
            val bitMatrix: BitMatrix = try {
                writer.encode(
                    params.rawString, params.format, params.width, params.height
                )
            } catch (e: Exception) {
                Timber.tag("QRCodeGeneration").e("Error generating BitMatrix: ${e.message}")
                throw e
            }

            if (bitMatrix.width == 0 || bitMatrix.height == 0) {
                Timber.tag("QRCodeGeneration").e("BitMatrix width or height is 0")
                throw Exception("Invalid BitMatrix dimensions")
            }

            val bitmap = bitMatrixToBitmap(bitMatrix)
            val imageUri = saveBitmapToFile(bitmap)
            val inputJson = createJsonFromContent(content)
            val entity = QrItemEntity(
                qrType = content::class.java.simpleName,
                inputData = inputJson,
                imageUri = imageUri.toString(),
                timestamp = System.currentTimeMillis()
            )
            qrItemDao.insert(entity)
            bitmap
        }

    private fun resolveFormatAndString(content: QrContent): EncodeParams {
        return when (content) {
            is QrContent.Call -> EncodeParams(
                "tel:${content.phoneNumber}", BarcodeFormat.QR_CODE, 512, 512
            )

            is QrContent.Sms -> EncodeParams(
                "sms:${content.phoneNumber}?body=${content.message}",
                BarcodeFormat.QR_CODE,
                512,
                512
            )

            is QrContent.Email -> EncodeParams(
                "mailto:${content.emailAddress}?subject=${content.subject}&body=${content.body}",
                BarcodeFormat.QR_CODE,
                512,
                512
            )

            is QrContent.Website -> EncodeParams(content.url, BarcodeFormat.QR_CODE, 512, 512)
            is QrContent.Text -> EncodeParams(content.text, BarcodeFormat.QR_CODE, 512, 512)
            is QrContent.Clipboard -> EncodeParams(content.clip, BarcodeFormat.QR_CODE, 512, 512)
            is QrContent.Wifi -> {
                val raw = "WIFI:S:${content.ssid};T:${content.encryption};P:${content.password};;"
                EncodeParams(raw, BarcodeFormat.QR_CODE, 512, 512)
            }

            is QrContent.Calendar -> {
                val raw = buildString {
                    append("BEGIN:VEVENT\n")
                    append("SUMMARY:${content.title}\n")
                    append("DESCRIPTION:${content.description}\n")
                    append("DTSTART:${content.startTime}\n")
                    append("DTEND:${content.endTime}\n")
                    append("END:VEVENT")
                }
                EncodeParams(raw, BarcodeFormat.QR_CODE, 512, 512)
            }

            is QrContent.Contact -> {
                val raw =
                    "BEGIN:VCARD\nVERSION:3.0\nFN:${content.name}\nTEL:${content.phoneNumber}\nEMAIL:${content.email}\nEND:VCARD"
                EncodeParams(raw, BarcodeFormat.QR_CODE, 512, 512)
            }

            is QrContent.Location -> {
                val raw = "geo:${content.latitude},${content.longitude}"
                EncodeParams(raw, BarcodeFormat.QR_CODE, 512, 512)
            }

//            is QrContent.Ean8 -> {
//                Log.d("QRCodeGeneration", "EAN-8 Code: ${content.code}")
//                // Validate that the EAN-8 code is exactly 8 digits and checksum is valid
//                if (content.code.length != 8 || !content.code.matches(Regex("^[0-9]{8}$"))) {
//                    throw IllegalArgumentException("Invalid EAN-8 code length")
//                }
//
//                if (!isValidEAN8(content.code)) {
//                    throw IllegalArgumentException("Invalid EAN-8 code checksum")
//                }
//
//                EncodeParams(content.code, BarcodeFormat.EAN_8, 512, 256)
//            }
//
//            is QrContent.Ean13 -> EncodeParams(content.code, BarcodeFormat.EAN_13, 512, 256)
//
//            is QrContent.UpcA -> {
//                Log.d("QRCodeGeneration", "UPC-A Code: ${content.code}")
//                // Validate that the UPC-A code is exactly 12 digits and checksum is valid
//                if (content.code.length != 12 || !content.code.matches(Regex("^[0-9]{12}$"))) {
//                    throw IllegalArgumentException("Invalid UPC-A code length")
//                }
//
//                if (!isValidUPC(content.code)) {
//                    throw IllegalArgumentException("Invalid UPC-A code checksum")
//                }
//
//                EncodeParams(content.code, BarcodeFormat.UPC_A, 512, 256)
//            }
//
//            is QrContent.UpcE -> {
//                Log.d("QRCodeGeneration", "UPC-E Code: ${content.code}")
//                // Validate that the UPC-E code is exactly 6 digits
//                if (content.code.length != 6 || !content.code.matches(Regex("^[0-9]{6}$"))) {
//                    throw IllegalArgumentException("Invalid UPC-E code length")
//                }
//
//                if (!isValidUPC(content.code)) {
//                    throw IllegalArgumentException("Invalid UPC-E code checksum")
//                }
//
//                EncodeParams(content.code, BarcodeFormat.UPC_E, 512, 256)
//            }

            is QrContent.Code39 -> EncodeParams(content.code, BarcodeFormat.CODE_39, 512, 256)
            is QrContent.Code128 -> EncodeParams(content.code, BarcodeFormat.CODE_128, 512, 256)
            is QrContent.Itf -> EncodeParams(content.code, BarcodeFormat.ITF, 512, 256)
            is QrContent.Pdf417 -> EncodeParams(content.code, BarcodeFormat.PDF_417, 512, 512)
            is QrContent.Codabar -> EncodeParams(content.code, BarcodeFormat.CODABAR, 512, 256)
            is QrContent.DataMatrix -> EncodeParams(
                content.code, BarcodeFormat.DATA_MATRIX, 512, 512
            )

            is QrContent.Aztec -> EncodeParams(content.code, BarcodeFormat.AZTEC, 512, 512)
        }
    }

    private fun bitMatrixToBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bmp
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri {
        val context = application
        val filename = "QR_${System.currentTimeMillis()}.png"
        val file = File(context.cacheDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return Uri.fromFile(file)
    }

    private fun createJsonFromContent(content: QrContent): String {
        return when (content) {
            is QrContent.Call -> """{"phoneNumber": "${content.phoneNumber}"}"""
            is QrContent.Sms -> """{"phoneNumber": "${content.phoneNumber}", "message": "${content.message}"}"""
            is QrContent.Email -> """{"emailAddress": "${content.emailAddress}", "subject": "${content.subject}", "body": "${content.body}"}"""
            is QrContent.Website -> """{"url": "${content.url}"}"""
            is QrContent.Text -> """{"text": "${content.text}"}"""
            is QrContent.Clipboard -> """{"clip": "${content.clip}"}"""
            is QrContent.Wifi -> """{"ssid": "${content.ssid}", "password": "${content.password}", "encryption": "${content.encryption}"}"""
            is QrContent.Calendar -> """{"title": "${content.title}", "description": "${content.description}", "startTime": "${content.startTime}", "endTime": "${content.endTime}"}"""
            is QrContent.Contact -> """{"name": "${content.name}", "phoneNumber": "${content.phoneNumber}", "email": "${content.email}"}"""
            is QrContent.Location -> """{"latitude": "${content.latitude}", "longitude": "${content.longitude}"}"""
//            is QrContent.Ean8 -> """{"code": "${content.code}"}"""
//            is QrContent.Ean13 -> """{"code": "${content.code}"}"""
//            is QrContent.UpcA -> """{"code": "${content.code}"}"""
//            is QrContent.UpcE -> """{"code": "${content.code}"}"""
            is QrContent.Code39 -> """{"code": "${content.code}"}"""
            is QrContent.Code128 -> """{"code": "${content.code}"}"""
            is QrContent.Itf -> """{"code": "${content.code}"}"""
            is QrContent.Pdf417 -> """{"code": "${content.code}"}"""
            is QrContent.Codabar -> """{"code": "${content.code}"}"""
            is QrContent.DataMatrix -> """{"code": "${content.code}"}"""
            is QrContent.Aztec -> """{"code": "${content.code}"}"""
        }
    }

    // Checksum validation methods for EAN-8 and UPC-A
    private fun isValidEAN8(code: String): Boolean {
        // Implement EAN-8 checksum validation logic here
        val sum = code.dropLast(1)
            .mapIndexed { index, c -> (c.toString().toInt() * (3 - (index % 2))) }
            .sum()

        val checkDigit = code.last().toString().toInt()
        val calculatedCheckDigit = (10 - (sum % 10)) % 10

        return checkDigit == calculatedCheckDigit
    }

    private fun isValidUPC(code: String): Boolean {
        val sumOdd = code.filterIndexed { index, _ -> index % 2 == 0 }
            .map { it.toString().toInt() }
            .sum()

        val sumEven = code.filterIndexed { index, _ -> index % 2 == 1 }
            .map { it.toString().toInt() }
            .sum()

        val totalSum = sumOdd * 3 + sumEven
        val checkDigit = code.last().toString().toInt()
        val calculatedCheckDigit = (10 - (totalSum % 10)) % 10

        return checkDigit == calculatedCheckDigit
    }
}
