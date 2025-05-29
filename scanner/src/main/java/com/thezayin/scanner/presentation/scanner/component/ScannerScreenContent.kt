package com.thezayin.scanner.presentation.scanner.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.thezayin.framework.components.ScannerOverlay
import com.thezayin.scanner.presentation.scanner.ScannerViewModel
import com.thezayin.scanner.presentation.scanner.state.ScannerState
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ScannerScreenContent(
    state: ScannerState,
    onBatchClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onFlashToggle: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onScanSuccess: (List<Pair<String, String>>) -> Unit,
    onCameraReady: (androidx.camera.core.Camera) -> Unit,
    viewModel: ScannerViewModel
) {
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Column {
            Spacer(modifier = Modifier.size(10.sdp))
            HeaderSection(
                onBatchClick = onBatchClick,
                onGalleryClick = onGalleryClick,
                onFlashToggle = onFlashToggle,
                isFlashOn = state.isFlashlightOn
            )
        }
    }, bottomBar = {
        ZoomControlsSection(
            primaryColor = viewModel.primaryColor,
            zoomLevel = state.zoomLevel,
            onZoomChange = onZoomChange,
            onZoomIn = onZoomIn,
            onZoomOut = onZoomOut
        )
    }) { paddingValues ->
        CameraPreview(
            onCameraReady = onCameraReady,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize(),
            onScanSuccess = onScanSuccess
        )
        ScannerOverlay(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }

    if (state.qrCodeFound == true) {
        android.widget.Toast.makeText(
            context,
            context.getString(R.string.qr_code_found, state.scannedResult),
            android.widget.Toast.LENGTH_SHORT
        ).show()
    } else if (state.qrCodeFound == false) {
        android.widget.Toast.makeText(
            context,
            context.getString(R.string.qr_code_not_found),
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
}