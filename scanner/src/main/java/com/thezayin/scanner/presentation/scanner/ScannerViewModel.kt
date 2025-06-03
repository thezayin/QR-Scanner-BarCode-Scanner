package com.thezayin.scanner.presentation.scanner

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.mlkit.vision.common.InputImage
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.framework.session.ScanSessionManager
import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.usecase.ScanQrUseCase
import com.thezayin.scanner.presentation.scanner.analyzer.BarcodeAnalyzer
import com.thezayin.scanner.presentation.scanner.event.ScannerEvent
import com.thezayin.scanner.presentation.scanner.state.ScannerState
import com.thezayin.values.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class ScannerViewModel(
    private val app: Application,
    private val scanQrUseCase: ScanQrUseCase,
    private val preferencesManager: PreferencesManager,
    private val sessionManager: ScanSessionManager,
    val remoteConfig: RemoteConfig,
    val adManager: InterstitialAdManager
) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    val primaryColor = preferencesManager.getPrimaryColor()
    private var screenNavigationAction: ((List<Pair<String, String>>) -> Unit)? = null

    private var currentZoomJob: Job? = null

    init {
        sessionManager.clearScanResults()
    }

    fun setScreenNavigationAction(navAction: (List<Pair<String, String>>) -> Unit) {
        this.screenNavigationAction = navAction
    }

    fun onCameraUseCasesReady(
        boundCamera: Camera, boundImageAnalysis: ImageAnalysis, boundImageCapture: ImageCapture
    ) {
        this.camera = boundCamera
        this.imageAnalysis = boundImageAnalysis
        this.imageCapture = boundImageCapture

        val cameraZoomState = boundCamera.cameraInfo.zoomState.value
        _state.update {
            it.copy(
                isCameraReady = true,
                zoomLevel = cameraZoomState?.zoomRatio ?: 1f,
                minZoomRatio = cameraZoomState?.minZoomRatio ?: 1f,
                maxZoomRatio = cameraZoomState?.maxZoomRatio ?: 1f
            )
        }
        startImageAnalysisSession()
    }

    private fun startImageAnalysisSession() {
        val localImageAnalysisUseCase = this.imageAnalysis ?: run {
            _state.value = _state.value.copy(error = app.getString(R.string.camera_not_ready_error))
            return
        }
        localImageAnalysisUseCase.clearAnalyzer()
        val newAnalyzer = BarcodeAnalyzer(onBarcodeScanned = { scannedText ->
            viewModelScope.launch {
                handleLiveCameraScanResult(scannedText)
            }
        }, shouldContinueScanningProvider = {
            _state.value.isBatchModeActive
        })
        localImageAnalysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(app), newAnalyzer)
    }

    private fun stopImageAnalysisSession() {
        imageAnalysis?.clearAnalyzer()
    }

    fun onEvent(event: ScannerEvent) {
        viewModelScope.launch {
            when (event) {
                ScannerEvent.StartBatchScan -> enterBatchMode()
                ScannerEvent.CancelBatchScan -> exitBatchModeAndPrepareForSingleScan()
                ScannerEvent.ConfirmBatchScan -> confirmAndProcessBatch()
                is ScannerEvent.ImageSelected -> handleGallerySingleImage(event.imageUri)
                is ScannerEvent.ImagesSelected -> handleGalleryBatchImages(event.imageUris)
                is ScannerEvent.ToggleFlashlight -> toggleFlashlight()
                is ScannerEvent.ChangeZoom -> updateZoomLevelUi(event.zoomLevel)
                is ScannerEvent.ShowError -> _state.value = _state.value.copy(error = event.message)
                is ScannerEvent.ProcessScanResultInternal -> {
                    Timber.tag("ScannerViewModel_onEven")
                        .w("ProcessScanResultInternal was unexpectedly called via general onEvent. This is for Analyzer callback only.")
                }
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private suspend fun handleLiveCameraScanResult(result: String) {
        if (state.value.isBatchModeActive) {
            if (!_state.value.batchScannedCodes.contains(result)) {
                triggerHapticFeedback()
                _state.update { it.copy(batchScannedCodes = it.batchScannedCodes + result) }
            }
        } else {
            stopImageAnalysisSession()
            try {
                val imagePath = captureImageAndGetPath()
                sessionManager.saveScanResult(imagePath, result)
                triggerHapticFeedback()
                this.screenNavigationAction?.invoke(listOf(imagePath to result))
                    ?: Timber.tag("ScannerViewModel_Handle")
                        .e("screenNavigationAction is NULL! Cannot navigate on single scan.")
            } catch (e: Exception) {
                Timber.e(e, "Error during image capture or processing in single scan mode")
                FirebaseCrashlytics.getInstance().recordException(e)
                _state.update {
                    it.copy(
                        error = app.getString(
                            R.string.qr_code_scan_error,
                            e.localizedMessage ?: "Unknown error during image capture"
                        )
                    )
                }
            } finally {
                startImageAnalysisSession()
            }
        }
    }

    private fun enterBatchMode() {
        if (!_state.value.isCameraReady || this.imageAnalysis == null) {
            _state.update { it.copy(error = app.getString(R.string.camera_not_ready_error)) }
            return
        }
        sessionManager.clearScanResults()
        _state.update { it.copy(isBatchModeActive = true, batchScannedCodes = emptySet()) }
        startImageAnalysisSession()
    }

    private fun exitBatchModeAndPrepareForSingleScan() {
        _state.update { it.copy(isBatchModeActive = false, batchScannedCodes = emptySet()) }
        startImageAnalysisSession()
    }

    private suspend fun confirmAndProcessBatch() {
        val codesToProcess = _state.value.batchScannedCodes.toList()
        _state.update { it.copy(isBatchModeActive = false, batchScannedCodes = emptySet()) }
        stopImageAnalysisSession()

        if (codesToProcess.isNotEmpty()) {
            var singleContextualImagePath: String? = null
            try {
                singleContextualImagePath = captureImageAndGetPath()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                _state.update { it.copy(error = app.getString(R.string.image_capture_failed_for_batch)) }
            }

            val navigationPayload = codesToProcess.map { code ->
                val imagePathToStore =
                    singleContextualImagePath ?: "batch_item_no_image_${System.currentTimeMillis()}"
                sessionManager.saveScanResult(
                    imagePathToStore, code
                )
                imagePathToStore to code
            }
            this.screenNavigationAction?.invoke(navigationPayload) ?: Timber.tag("ScannerViewModel")
                .e("screenNavigationAction is NULL on batch confirm! Cannot navigate.")
        } else {
            Toast.makeText(
                app,
                app.getString(R.string.no_items_scanned_in_batch),
                Toast.LENGTH_SHORT
            ).show()
        }
        startImageAnalysisSession()
    }

    private suspend fun handleGallerySingleImage(imageUriString: String) {
        try {
            val image = InputImage.fromFilePath(app, imageUriString.toUri())
            when (val scanResult = scanQrUseCase.execute(image)) {
                is Result.Success -> {
                    triggerHapticFeedback()
                    sessionManager.saveScanResult(imageUriString, scanResult.data.content)
                    this.screenNavigationAction?.invoke(listOf(imageUriString to scanResult.data.content))
                        ?: Timber.tag("ScannerViewModel")
                            .e("screenNavigationAction is NULL for gallery single. Cannot navigate.")
                }

                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            error = scanResult.exception.message
                                ?: app.getString(R.string.qr_code_not_found_gallery)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            _state.update { it.copy(error = app.getString(R.string.error_processing_gallery_image)) }
        }
    }

    private suspend fun handleGalleryBatchImages(imageUriStrings: List<String>) {
        val successfulScans = mutableListOf<Pair<String, String>>()
        val uniqueCodesFoundInBatch = mutableSetOf<String>()
        imageUriStrings.forEach { uriString ->
            try {
                val image = InputImage.fromFilePath(app, uriString.toUri())
                when (val scanResult = scanQrUseCase.execute(image)) {
                    is Result.Success -> {
                        if (uniqueCodesFoundInBatch.add(scanResult.data.content)) {
                            sessionManager.saveScanResult(uriString, scanResult.data.content)
                            successfulScans.add(uriString to scanResult.data.content)
                        }
                    }

                    is Result.Failure -> {
                        Timber.d("No QR code found in image $uriString: ${scanResult.exception.message}")
                    }
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Timber.e(e, "Error processing gallery image from URI: $uriString")
            }
        }

        if (successfulScans.isNotEmpty()) {
            triggerHapticFeedback()
            this.screenNavigationAction?.invoke(successfulScans) ?: Timber.tag("ScannerViewModel")
                .e("screenNavigationAction is NULL for gallery batch. Cannot navigate.")
        } else {
            _state.update { it.copy(error = app.getString(R.string.qr_code_not_found_in_any_image)) }
        }
    }

    private suspend fun captureImageAndGetPath(): String {
        val localImageCapture =
            this.imageCapture ?: throw IllegalStateException("ImageCapture is not initialized.")
        return suspendCancellableCoroutine { cont ->
            val photoFile = File(
                app.getExternalFilesDir(null) ?: app.filesDir,
                "ScanCapture_${System.currentTimeMillis()}.jpg"
            )
            photoFile.parentFile?.mkdirs()
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            localImageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(app),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        cont.resume(photoFile.absolutePath)
                    }

                    override fun onError(exc: ImageCaptureException) {
                        cont.resumeWithException(exc)
                    }
                })
        }
    }

    private fun toggleFlashlight() {
        val newFlashState = !_state.value.isFlashlightOn
        camera?.cameraControl?.enableTorch(newFlashState)?.addListener({
            _state.update { it.copy(isFlashlightOn = newFlashState) }
        }, ContextCompat.getMainExecutor(app))
    }

    fun updateZoomLevelUi(requestedLevel: Float) {
        val currentMinZoom = _state.value.minZoomRatio
        val currentMaxZoom = _state.value.maxZoomRatio
        val clampedLevel = requestedLevel.coerceIn(currentMinZoom, currentMaxZoom)
        _state.update { it.copy(zoomLevel = clampedLevel) }
        if (clampedLevel != camera?.cameraInfo?.zoomState?.value?.zoomRatio) {
            currentZoomJob?.cancel()
            currentZoomJob = viewModelScope.launch {
                try {
                    camera?.cameraControl?.setZoomRatio(clampedLevel)?.await()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    _state.update {
                        it.copy(
                            error = app.getString(
                                R.string.zoom_control_error_format,
                                e.localizedMessage ?: "Unknown error"
                            )
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun triggerHapticFeedback() {
        val vibrateEnabled = preferencesManager.getVibrateEnabled()
        val beepEnabled = preferencesManager.getBeepEnabled()

        if (!vibrateEnabled && !beepEnabled) {
            return
        }

        if (vibrateEnabled) {
            val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator?.hasVibrator() == true) {
                try {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            100,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Timber.e(e, "Error triggering vibration")
                }
            } else {
                Timber.w("Vibration requested but device has no vibrator or service not available.")
            }
        }

        if (beepEnabled) {
            try {
                val mediaPlayer = MediaPlayer.create(app, R.raw.scanner_sound)
                mediaPlayer?.setOnCompletionListener { mp ->
                    try {
                        mp.release()
                    } catch (e: IllegalStateException) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e); Timber.e(
                            e,
                            "Error releasing MediaPlayer on completion"
                        )
                    }
                }
                mediaPlayer?.setOnErrorListener { mp, what, extra ->
                    Timber.e("MediaPlayer error occurred: what=$what, extra=$extra")
                    try {
                        mp.release()
                    } catch (e: IllegalStateException) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e); Timber.e(
                            e,
                            "Error releasing MediaPlayer on error"
                        )
                    }
                    true
                }
                mediaPlayer?.start()
                if (mediaPlayer == null) {
                    Timber.w("MediaPlayer.create returned null. Sound resource might be missing or invalid.")
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Timber.e(e, "Error creating or starting MediaPlayer for beep sound")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopImageAnalysisSession()
        currentZoomJob?.cancel()
    }
}