package com.thezayin.scanner.presentation.scanner

import android.app.Activity
import android.net.Uri
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.thezayin.scanner.presentation.scanner.component.ScannerScreenContent
import com.thezayin.scanner.presentation.scanner.event.ScannerEvent
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = koinInject(),
    onSuccessfulScanNavigation: (List<Pair<String, String>>) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val activity = LocalActivity.current as Activity
    LaunchedEffect(viewModel, onSuccessfulScanNavigation) {
        viewModel.setScreenNavigationAction(onSuccessfulScanNavigation)
    }

    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), onResult = { uri: Uri? ->
            uri?.let {
                viewModel.onEvent(ScannerEvent.ImageSelected(it.toString()))
            }
        })

    val batchImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(), onResult = { uris: List<Uri>? ->
            uris?.let { selectedUris ->
                if (selectedUris.isNotEmpty()) {
                    viewModel.adManager.showAd(
                        activity = activity,
                        showAd = viewModel.remoteConfig.adConfigs.adOnBatchSelection,
                        onNext = {
                            viewModel.onEvent(ScannerEvent.ImagesSelected(selectedUris.map { it.toString() }))
                        })
                }
            }
        })

    LaunchedEffect(Unit) {
        viewModel.adManager.loadAd(activity)
    }

    val launchGalleryAction = {
        if (state.isBatchModeActive) {
            batchImagePickerLauncher.launch("image/*")
        } else {
            singleImagePickerLauncher.launch("image/*")
        }
    }

    ScannerScreenContent(
        state = state, viewModel = viewModel, onHeaderGalleryClick = launchGalleryAction
    )
}