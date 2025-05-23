package com.thezayin.generate.presentation

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.ads.admob.domain.repository.RewardedAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.generate.domain.model.QrContent
import com.thezayin.generate.domain.model.QrType
import com.thezayin.generate.domain.usecase.GenerateQrUseCase
import com.thezayin.generate.presentation.event.GenerateEvent
import com.thezayin.generate.presentation.state.GenerateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class GenerateViewModel(
    application: Application,
    private val generateQrUseCase: GenerateQrUseCase,
    val remoteConfig: RemoteConfig,
    val pref: PreferencesManager,
    val adManager: InterstitialAdManager,
    val rewardedAdManager: RewardedAdManager,
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(GenerateState())
    val state: StateFlow<GenerateState> = _state

    fun onEvent(event: GenerateEvent) {
        when (event) {
            is GenerateEvent.SelectType -> {
                _state.value = _state.value.copy(
                    selectedType = event.type,
                    showMainListOnly = false,
                    generatedQrBitmap = null
                )
            }

            is GenerateEvent.UpdateCallNumber ->
                _state.value = _state.value.copy(callNumber = event.number)

            is GenerateEvent.UpdateSmsNumber ->
                _state.value = _state.value.copy(smsNumber = event.number)

            is GenerateEvent.UpdateSmsMessage ->
                _state.value = _state.value.copy(smsMessage = event.message)

            is GenerateEvent.UpdateEmailAddress ->
                _state.value = _state.value.copy(emailAddress = event.address)

            is GenerateEvent.UpdateEmailSubject ->
                _state.value = _state.value.copy(emailSubject = event.subject)

            is GenerateEvent.UpdateEmailBody ->
                _state.value = _state.value.copy(emailBody = event.body)

            is GenerateEvent.UpdateWebsiteUrl ->
                _state.value = _state.value.copy(websiteUrl = event.url)

            is GenerateEvent.UpdateText ->
                _state.value = _state.value.copy(text = event.text)

            is GenerateEvent.UpdateClipboard ->
                _state.value = _state.value.copy(clip = event.clip)

            is GenerateEvent.UpdateWifiSsid ->
                _state.value = _state.value.copy(wifiSsid = event.ssid)

            is GenerateEvent.UpdateWifiPassword ->
                _state.value = _state.value.copy(wifiPassword = event.password)

            is GenerateEvent.UpdateWifiEncryption ->
                _state.value = _state.value.copy(wifiEncryption = event.encryption)

            is GenerateEvent.UpdateCalendarTitle ->
                _state.value = _state.value.copy(calendarTitle = event.title)

            is GenerateEvent.UpdateCalendarDescription ->
                _state.value = _state.value.copy(calendarDescription = event.description)

            is GenerateEvent.UpdateCalendarStart ->
                _state.value = _state.value.copy(calendarStart = event.start)

            is GenerateEvent.UpdateCalendarEnd ->
                _state.value = _state.value.copy(calendarEnd = event.end)

            is GenerateEvent.UpdateContactName ->
                _state.value = _state.value.copy(contactName = event.name)

            is GenerateEvent.UpdateContactPhone ->
                _state.value = _state.value.copy(contactPhone = event.phone)

            is GenerateEvent.UpdateContactEmail ->
                _state.value = _state.value.copy(contactEmail = event.email)

            is GenerateEvent.UpdateLocationLat ->
                _state.value = _state.value.copy(locationLat = event.lat)

            is GenerateEvent.UpdateLocationLong ->
                _state.value = _state.value.copy(locationLong = event.long)

//            is GenerateEvent.UpdateEan8 ->
//                _state.value = _state.value.copy(ean8Code = event.code)
//
//            is GenerateEvent.UpdateEan13 ->
//                _state.value = _state.value.copy(ean13Code = event.code)
//
//            is GenerateEvent.UpdateUpcA ->
//                _state.value = _state.value.copy(upcACode = event.code)
//
//            is GenerateEvent.UpdateUpcE ->
//                _state.value = _state.value.copy(upcECode = event.code)

            is GenerateEvent.UpdateCode39 ->
                _state.value = _state.value.copy(code39Code = event.code)

            is GenerateEvent.UpdateCode128 ->
                _state.value = _state.value.copy(code128Code = event.code)

            is GenerateEvent.UpdateItf ->
                _state.value = _state.value.copy(itfCode = event.code)

            is GenerateEvent.UpdatePdf417 ->
                _state.value = _state.value.copy(pdf417Code = event.code)

            is GenerateEvent.UpdateCodabar ->
                _state.value = _state.value.copy(codabarCode = event.code)

            is GenerateEvent.UpdateDataMatrix ->
                _state.value = _state.value.copy(dataMatrixCode = event.code)

            is GenerateEvent.UpdateAztec ->
                _state.value = _state.value.copy(aztecCode = event.code)

            GenerateEvent.GenerateQrCode -> generateQr()
            GenerateEvent.BackPressed -> {
                if (!_state.value.showMainListOnly) {
                    _state.value =
                        _state.value.copy(showMainListOnly = true, generatedQrBitmap = null)
                }
            }

            GenerateEvent.DownloadQrCode -> {
                _state.value.generatedQrBitmap?.let { bmp ->
                    downloadQrCode(bmp)
                    _state.value = _state.value.copy(showDownloadSuccess = true)
                }
            }

            GenerateEvent.ShareQrCode -> {
                _state.value.generatedQrBitmap?.let { bmp ->
                    shareQrCode(bmp)
                }
            }

            GenerateEvent.DismissDownloadSuccess -> {
                _state.value = _state.value.copy(showDownloadSuccess = false)
            }
        }
    }

    fun initManager(activity: Activity) {
        adManager.loadAd(activity)
        rewardedAdManager.loadAd(activity)
    }

    private fun generateQr() {
        viewModelScope.launch {
            try {
                val content = buildQrContent()
                Log.d("jeje", "$content")
                val bmp = generateQrUseCase(content)
                _state.value = _state.value.copy(generatedQrBitmap = bmp)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun buildQrContent(): QrContent {
        val s = _state.value
        return when (s.selectedType) {
            QrType.CALL -> QrContent.Call(phoneNumber = s.callNumber)
            QrType.SMS -> QrContent.Sms(s.smsNumber, s.smsMessage)
            QrType.EMAIL -> QrContent.Email(s.emailAddress, s.emailSubject, s.emailBody)
            QrType.WEBSITE -> QrContent.Website(s.websiteUrl)
            QrType.TEXT -> QrContent.Text(s.text)
            QrType.CLIPBOARD -> QrContent.Clipboard(s.clip)
            QrType.WIFI -> QrContent.Wifi(s.wifiSsid, s.wifiPassword, s.wifiEncryption)
            QrType.CALENDAR -> QrContent.Calendar(
                title = s.calendarTitle,
                description = s.calendarDescription,
                startTime = s.calendarStart.toLongOrNull() ?: 0L,
                endTime = s.calendarEnd.toLongOrNull() ?: 0L
            )

            QrType.CONTACT -> QrContent.Contact(s.contactName, s.contactPhone, s.contactEmail)
            QrType.LOCATION -> QrContent.Location(s.locationLat, s.locationLong)
//            QrType.EAN_8 -> QrContent.Ean8(s.ean8Code)
//            QrType.EAN_13 -> QrContent.Ean13(s.ean13Code)
//            QrType.UPC_A -> QrContent.UpcA(s.upcACode)
//            QrType.UPC_E -> QrContent.UpcE(s.upcECode)
            QrType.CODE_39 -> QrContent.Code39(s.code39Code)
            QrType.CODE_128 -> QrContent.Code128(s.code128Code)
            QrType.ITF -> QrContent.Itf(s.itfCode)
            QrType.PDF_417 -> QrContent.Pdf417(s.pdf417Code)
            QrType.CODABAR -> QrContent.Codabar(s.codabarCode)
            QrType.DATAMATRIX -> QrContent.DataMatrix(s.dataMatrixCode)
            QrType.AZTEC -> QrContent.Aztec(s.aztecCode)
        }
    }

    private fun downloadQrCode(bitmap: Bitmap) {
        val context = getApplication<Application>()
        val filename = "QR_${System.currentTimeMillis()}.png"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
        }
        val resolver = context.contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
    }

    private fun shareQrCode(bitmap: Bitmap) {
        val context = getApplication<Application>()
        val filename = "QR_${System.currentTimeMillis()}.png"
        val cachePath = File(context.cacheDir, "images")
        if (!cachePath.exists()) cachePath.mkdirs()
        val file = File(cachePath, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Share QR Code").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
