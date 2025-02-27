package com.thezayin.generate.data.repository

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.thezayin.databases.dao.QrItemDao
import com.thezayin.databases.entity.QrItemEntity
import com.thezayin.generate.domain.model.QrContent
import com.thezayin.generate.domain.repository.QrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            val writer = MultiFormatWriter()
            val bitMatrix: BitMatrix = writer.encode(
                params.rawString, params.format, params.width, params.height
            )
            val bitmap = bitMatrixToBitmap(bitMatrix)
            val imageUri = saveBitmapToFile(bitmap)
            val inputJson = createJsonFromContent(content)
            val entity = QrItemEntity(
                qrType = content::class.java.simpleName, // or use a custom mapping if desired
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

            is QrContent.Ean8 -> EncodeParams(content.code, BarcodeFormat.EAN_8, 512, 256)
            is QrContent.Ean13 -> EncodeParams(content.code, BarcodeFormat.EAN_13, 512, 256)
            is QrContent.UpcA -> EncodeParams(content.code, BarcodeFormat.UPC_A, 512, 256)
            is QrContent.UpcE -> EncodeParams(content.code, BarcodeFormat.UPC_E, 512, 256)
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
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
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
            is QrContent.Ean8 -> """{"code": "${content.code}"}"""
            is QrContent.Ean13 -> """{"code": "${content.code}"}"""
            is QrContent.UpcA -> """{"code": "${content.code}"}"""
            is QrContent.UpcE -> """{"code": "${content.code}"}"""
            is QrContent.Code39 -> """{"code": "${content.code}"}"""
            is QrContent.Code128 -> """{"code": "${content.code}"}"""
            is QrContent.Itf -> """{"code": "${content.code}"}"""
            is QrContent.Pdf417 -> """{"code": "${content.code}"}"""
            is QrContent.Codabar -> """{"code": "${content.code}"}"""
            is QrContent.DataMatrix -> """{"code": "${content.code}"}"""
            is QrContent.Aztec -> """{"code": "${content.code}"}"""
        }
    }
}