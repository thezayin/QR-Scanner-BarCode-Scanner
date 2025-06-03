package com.thezayin.qrscanner.ui.language.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thezayin.framework.preferences.Language
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

sealed class LanguageScreenResult {
    object GoBackToCaller : LanguageScreenResult()
    object FinalizeOnboarding : LanguageScreenResult()
    object RecreateApp : LanguageScreenResult()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    onNavigateFinished: (result: LanguageScreenResult) -> Unit,
    isFromOnboarding: Boolean,
    viewModel: LanguageViewModel = koinInject()
) {
    val languages by viewModel.languages.collectAsStateWithLifecycle()
    val selectedLanguageCode by viewModel.selectedUiLanguageCode.collectAsStateWithLifecycle()
    val downloadStatus by viewModel.downloadStatus.collectAsStateWithLifecycle(initialValue = LanguageViewModel.DownloadStatus.Idle)
    val isDownloadInProgress by viewModel.isDownloadInProgress.collectAsStateWithLifecycle()
    val isCurrentlySelectedLanguageReady by viewModel.isCurrentlySelectedLanguageReady.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.navigateAction.collectLatest { action ->
            when (action) {
                is LanguageViewModel.NavigateAction.PopStack -> onNavigateFinished(LanguageScreenResult.GoBackToCaller)
                is LanguageViewModel.NavigateAction.RecreateActivity -> onNavigateFinished(LanguageScreenResult.RecreateApp)
                is LanguageViewModel.NavigateAction.CompleteOnboarding -> onNavigateFinished(LanguageScreenResult.FinalizeOnboarding)
            }
        }
    }

    LaunchedEffect(downloadStatus) {
        when (val status = downloadStatus) {
            is LanguageViewModel.DownloadStatus.Success -> {
                val langName =
                    languages.find { it.code == status.langCode }?.name ?: status.langCode
                Toast.makeText(
                    context,
                    context.getString(R.string.language_download_success, langName),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is LanguageViewModel.DownloadStatus.Error -> {
                val langName =
                    languages.find { it.code == status.langCode }?.name ?: status.langCode
                Toast.makeText(
                    context,
                    context.getString(
                        R.string.download_failed_for,
                        langName
                    ) + ": ${status.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.onBackClicked()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
                Text(
                    text = stringResource(id = R.string.language),
                    fontSize = 16.ssp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                val isSaveButtonEnabled = !isDownloadInProgress && isCurrentlySelectedLanguageReady

                IconButton(
                    onClick = { viewModel.onSaveClicked(isFromOnboarding) },
                    enabled = isSaveButtonEnabled
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        tint = if (isSaveButtonEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        ),
                        modifier = Modifier.size(24.dp),
                        contentDescription = stringResource(id = R.string.save_language)
                    )
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 15.sdp, vertical = 10.sdp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(languages) { language ->
                        LanguageItem(
                            language = language,
                            isSelected = language.code == selectedLanguageCode,
                            onLanguageClick = {
                                viewModel.onLanguageSelected(it.code)
                            },
                            enabled = !isDownloadInProgress
                        )
                        Spacer(modifier = Modifier.height(8.sdp))
                    }
                }

                if (downloadStatus is LanguageViewModel.DownloadStatus.Progress) {
                    val langName =
                        languages.find { it.code == (downloadStatus as LanguageViewModel.DownloadStatus.Progress).langCode }?.name
                            ?: (downloadStatus as LanguageViewModel.DownloadStatus.Progress).langCode

                    LanguageDownloadDialog(
                        selectedLanguageName = langName
                    )
                }
                else if (downloadStatus is LanguageViewModel.DownloadStatus.Error) {
                    val langName =
                        languages.find { it.code == (downloadStatus as LanguageViewModel.DownloadStatus.Error).langCode }?.name
                            ?: (downloadStatus as LanguageViewModel.DownloadStatus.Error).langCode

                    LanguageErrorDialog(
                        errorMessage = (downloadStatus as LanguageViewModel.DownloadStatus.Error).message,
                        selectedLanguageName = langName,
                        onDismiss = { viewModel.resetDownloadStatus() }
                    )
                }
            }
        }
    )
}

@Composable
fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    onLanguageClick: (Language) -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                onLanguageClick(language)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceContainer,
            contentColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp, vertical = 5.sdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = language.flagEmoji ?: "🌐",
                    fontSize = 18.ssp
                )
            }
            Text(
                text = language.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.ssp,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.selected),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun LanguageDownloadDialog(selectedLanguageName: String) {
    AlertDialog(
        onDismissRequest = {
        },
        title = {
            Text(stringResource(R.string.downloading_language_for, selectedLanguageName))
        },
        text = {
            Column {
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )
                Spacer(Modifier.height(16.dp))
            }
        },
        confirmButton = {
        },
        dismissButton = {
        }
    )
}

@Composable
fun LanguageErrorDialog(errorMessage: String, selectedLanguageName: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.download_failed_for, selectedLanguageName)) },
        text = { Text(stringResource(R.string.error_message_format, errorMessage)) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
}