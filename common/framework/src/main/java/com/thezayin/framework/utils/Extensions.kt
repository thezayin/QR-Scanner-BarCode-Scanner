package com.thezayin.framework.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.thezayin.values.R
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun parseWifiResult(result: String): Pair<String, String>? {
    val trimmed = result.trim()
    if (!trimmed.startsWith("wifi:", ignoreCase = true)) return null
    val content = trimmed.substringAfter("wifi:", "")
    val parts = content.split(";")
    var ssid = ""
    var password = ""
    for (part in parts) {
        when {
            part.startsWith("S:", ignoreCase = true) -> ssid = part.substringAfter("S:", "")
            part.startsWith("P:", ignoreCase = true) -> password = part.substringAfter("P:", "")
        }
    }
    return if (ssid.isNotEmpty()) Pair(ssid, password) else null
}

fun saveImageToExternalStorage(context: Context, imageUri: String?): Boolean {
    if (imageUri.isNullOrEmpty()) {
        Timber.tag("SaveImage").e("Image URI is null or empty")
        return false
    }

    val filePath = Uri.parse(imageUri).path ?: run {
        Timber.tag("SaveImage").e("Failed to parse image URI: $imageUri")
        return false
    }

    Timber.tag("SaveImage").d("Extracted file path: $filePath")
    val file = File(filePath)
    if (!file.exists()) {
        Timber.tag("SaveImage").e("File does not exist at the specified path")
        return false
    }

    val bitmap: Bitmap? = BitmapFactory.decodeFile(filePath)
    if (bitmap == null) {
        Timber.tag("SaveImage").e("Failed to decode the image from the file")
        return false
    }

    val outputFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "${UUID.randomUUID()}.png"
    )

    try {
        FileOutputStream(outputFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        Timber.tag("SaveImage").d("Image saved successfully to: ${outputFile.absolutePath}")
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

fun shareImage(context: Context, imageUri: String) {
    val filePath = Uri.parse(imageUri).path
        ?: throw IllegalArgumentException("Invalid image URI: $imageUri")

    val file = File(filePath)
    val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun getQrTypeIcon(qrType: String): Int {
    return when (qrType.uppercase()) {
        "CALL" -> R.drawable.ic_call
        "SMS" -> R.drawable.ic_sms
        "EMAIL" -> R.drawable.ic_email
        "WEBSITE" -> R.drawable.ic_website
        "TEXT" -> R.drawable.ic_text
        "CLIPBOARD" -> R.drawable.ic_clipboard
        "WIFI" -> R.drawable.ic_wifi
        "CALENDAR" -> R.drawable.ic_calendar
        "CONTACT" -> R.drawable.ic_contact
        "LOCATION" -> R.drawable.ic_location
        "EAN_8", "EAN_13", "UPC_A", "UPC_E" -> R.drawable.ic_upc
        "CODE_39", "CODE_128", "ITF", "PDF_417", "CODABAR" -> R.drawable.ic_barcode_type
        "DATAMATRIX" -> R.drawable.ic_datamatrix
        "AZTEC" -> R.drawable.ic_itza
        else -> R.drawable.ic_upc
    }
}

fun typeToIcon(type: String): Int {
    return when (type) {
        "CALL" -> R.drawable.ic_call
        "SMS" -> R.drawable.ic_sms
        "EMAIL" -> R.drawable.ic_email
        "WEBSITE" -> R.drawable.ic_website
        "TEXT" -> R.drawable.ic_text
        "CLIPBOARD" -> R.drawable.ic_clipboard
        "WIFI" -> R.drawable.ic_wifi
        "CALENDAR" -> R.drawable.ic_calendar
        "CONTACT" -> R.drawable.ic_contact
        "LOCATION" -> R.drawable.ic_location
        "EAN_8", "EAN_13", "UPC_A", "UPC_E" -> R.drawable.ic_upc
        "CODE_39", "CODE_128", "ITF", "PDF_417", "CODABAR" -> R.drawable.ic_barcode_type
        "DATAMATRIX" -> R.drawable.ic_datamatrix
        "AZTEC" -> R.drawable.ic_itza
        else -> R.drawable.ic_barcode
    }
}

fun getDisplayText(result: String): String {
    return when {
        result.startsWith("tel:") -> "Call ${result.removePrefix("tel:")}"
        result.startsWith("sms:") -> "Send SMS to ${
            result.removePrefix("sms:").substringBefore("?")
        }"

        result.startsWith("mailto:") -> "Send Email to ${result.removePrefix("mailto:")}"
        result.startsWith("http://") || result.startsWith("https://") -> "Open Website"
        result.startsWith("wifi:") -> "Connect to WiFi"
        else -> result
    }
}

fun Context.openTerms(){
    val url = "https://bougielabs.com/terms-and-conditions/"
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    this.startActivity(intent)
}

fun Context.openPrivacy(){
    val url = "https://bougielabs.com/privacy-policy/"
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    this.startActivity(intent)
}