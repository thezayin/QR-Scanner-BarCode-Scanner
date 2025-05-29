@file:Suppress("DEPRECATION")

package com.thezayin.scanner.presentation.scanner.component

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.scanner.presentation.scanner.ScannerViewModel
import com.thezayin.scanner.presentation.scanner.event.ScannerEvent
import com.thezayin.values.R

@SuppressLint("StringFormatInvalid")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    viewModel: ScannerViewModel,
    onCameraSuccessfullyBound: ((Camera) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var previewViewInstance by remember { mutableStateOf<PreviewView?>(null) }
    LaunchedEffect(lifecycleOwner, previewViewInstance) {
        if (previewViewInstance == null) {
            Log.w("CameraPreview", "PreviewView instance is null, delaying camera setup.")
            return@LaunchedEffect
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
                val preview = Preview.Builder().build()
                val imageAnalysis = ImageAnalysis.Builder().setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalysis, imageCapture
                )
                preview.surfaceProvider = previewViewInstance!!.surfaceProvider
                viewModel.onCameraUseCasesReady(camera, imageAnalysis, imageCapture)
                onCameraSuccessfullyBound?.invoke(camera)

            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                viewModel.onEvent(
                    ScannerEvent.ShowError(
                        context.getString(
                            R.string.qr_code_scan_error, e.localizedMessage
                        )
                    )
                )
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                this.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
            }.also {
                if (previewViewInstance == null) {
                    previewViewInstance = it
                }
            }
        }, modifier = modifier.fillMaxSize()
    )
}