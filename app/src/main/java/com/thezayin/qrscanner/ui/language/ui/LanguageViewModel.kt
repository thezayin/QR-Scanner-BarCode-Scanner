package com.thezayin.qrscanner.ui.language.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.thezayin.framework.preferences.Language
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.qrscanner.ui.language.repo.LanguageRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import kotlin.math.max

@Suppress("PrivatePropertyName")
class LanguageViewModel(
    private val languageRepository: LanguageRepository,
    private val preferencesManager: PreferencesManager,
    private val splitInstallManager: SplitInstallManager
) : ViewModel() {

    private val _languages = MutableStateFlow<List<Language>>(emptyList())
    val languages: StateFlow<List<Language>> = _languages.asStateFlow()

    private val _selectedUiLanguageCode = MutableStateFlow<String?>(null)
    val selectedUiLanguageCode: StateFlow<String?> = _selectedUiLanguageCode.asStateFlow()

    private val _downloadStatus = Channel<DownloadStatus>()
    val downloadStatus = _downloadStatus.receiveAsFlow()

    private val _isDownloadInProgress = MutableStateFlow(false)
    val isDownloadInProgress: StateFlow<Boolean> = _isDownloadInProgress.asStateFlow()

    private val _isCurrentlySelectedLanguageReady = MutableStateFlow(false)
    val isCurrentlySelectedLanguageReady: StateFlow<Boolean> =
        _isCurrentlySelectedLanguageReady.asStateFlow()

    private val _navigateAction = MutableSharedFlow<NavigateAction>()
    val navigateAction: SharedFlow<NavigateAction> = _navigateAction.asSharedFlow()

    private var splitInstallListener: SplitInstallStateUpdatedListener? = null
    private var currentlyDownloadingLanguageCode: String? = null
    private var _downloadStartTime: Long = 0L

    private val MIN_DIALOG_DISPLAY_DURATION_MS = 2000L

    sealed class NavigateAction {
        object PopStack : NavigateAction()
        object RecreateActivity :
            NavigateAction()

        object CompleteOnboarding :
            NavigateAction()
    }

    sealed class DownloadStatus {
        data class Progress(val bytesDownloaded: Long, val totalBytes: Long, val langCode: String) :
            DownloadStatus()

        data class Success(val langCode: String) : DownloadStatus()
        data class Error(val message: String, val langCode: String) : DownloadStatus()
        object Idle : DownloadStatus()
    }

    init {
        loadLanguages()
        registerSplitInstallListener()
    }

    private fun registerSplitInstallListener() {
        splitInstallListener = SplitInstallStateUpdatedListener { state ->
            val langCodeBeingHandled = state.languages().firstOrNull()
            val downloadId = state.sessionId()
            Timber.tag("LanguageViewModel")
                .d("SplitInstall state changed: ${state.status()} for $langCodeBeingHandled (Session ${state.sessionId()})")

            if (currentlyDownloadingLanguageCode != null && langCodeBeingHandled != currentlyDownloadingLanguageCode && langCodeBeingHandled != null) {
                Timber.tag("LanguageViewModel")
                    .d("Ignoring state update for ${langCodeBeingHandled}, current download is for ${currentlyDownloadingLanguageCode}.")
                return@SplitInstallStateUpdatedListener
            }
            if (currentlyDownloadingLanguageCode == null && langCodeBeingHandled != null && state.status() != SplitInstallSessionStatus.INSTALLED) {
                Timber.tag("LanguageViewModel")
                    .w("Received unexpected status for $langCodeBeingHandled when no active download is tracked. Resetting state.")
                resetDownloadState()
                return@SplitInstallStateUpdatedListener
            }


            when (state.status()) {
                SplitInstallSessionStatus.PENDING,
                SplitInstallSessionStatus.DOWNLOADING -> {
                    viewModelScope.launch {
                        if (_downloadStartTime == 0L) {
                            _downloadStartTime = System.currentTimeMillis()
                            Timber.tag("LanguageViewModel")
                                .d("Download started at $_downloadStartTime for $currentlyDownloadingLanguageCode")
                        }
                        _isDownloadInProgress.value = true
                        _downloadStatus.send(
                            DownloadStatus.Progress(
                                state.bytesDownloaded(),
                                state.totalBytesToDownload(),
                                currentlyDownloadingLanguageCode ?: (langCodeBeingHandled
                                    ?: "unknown")
                            )
                        )
                    }
                }

                SplitInstallSessionStatus.INSTALLED -> {
                    viewModelScope.launch {
                        val languageJustInstalled =
                            langCodeBeingHandled ?: currentlyDownloadingLanguageCode ?: "unknown"
                        val endTime = System.currentTimeMillis()
                        val elapsedTime = endTime - _downloadStartTime
                        val remainingDelay = max(0L, MIN_DIALOG_DISPLAY_DURATION_MS - elapsedTime)
                        Timber.tag("LanguageViewModel")
                            .d("Download INSTALLED for $languageJustInstalled. Elapsed: $elapsedTime ms, Delaying for: $remainingDelay ms")
                        delay(remainingDelay)

                        _downloadStatus.send(DownloadStatus.Success(languageJustInstalled))
                        Timber.tag("LanguageViewModel")
                            .d("Language successfully processed after delay: $languageJustInstalled")

                        if (_selectedUiLanguageCode.value == languageJustInstalled) {
                            _isCurrentlySelectedLanguageReady.value = true
                            Timber.tag("LanguageViewModel")
                                .d("Selected UI language (${languageJustInstalled}) now marked as ready for save after download success.")
                        } else {
                            Timber.tag("LanguageViewModel")
                                .d("Downloaded language ($languageJustInstalled) does not match current selection (${_selectedUiLanguageCode.value}). Not marking as ready (still need manual selection/re-eval).")
                        }

                        resetDownloadState()
                    }
                }

                SplitInstallSessionStatus.FAILED -> {
                    viewModelScope.launch {
                        val languageInError =
                            langCodeBeingHandled ?: currentlyDownloadingLanguageCode ?: "unknown"
                        val endTime = System.currentTimeMillis()
                        val elapsedTime = endTime - _downloadStartTime
                        val remainingDelay = max(0L, MIN_DIALOG_DISPLAY_DURATION_MS - elapsedTime)
                        Timber.tag("LanguageViewModel")
                            .d("Download FAILED for $languageInError. Elapsed: $elapsedTime ms, Delaying for: $remainingDelay ms")
                        delay(remainingDelay)

                        val errorCode = state.errorCode()
                        val errorMessage = "Failed to download language. Code: $errorCode"
                        _downloadStatus.send(DownloadStatus.Error(errorMessage, languageInError))
                        Timber.tag("LanguageViewModel")
                            .e("Failed to start language download for $languageInError: Code $errorCode")

                        if (_selectedUiLanguageCode.value == languageInError) {
                            _isCurrentlySelectedLanguageReady.value = false
                        }
                        resetDownloadState()
                    }
                }

                SplitInstallSessionStatus.CANCELED -> {
                    viewModelScope.launch {
                        val languageCanceled =
                            langCodeBeingHandled ?: currentlyDownloadingLanguageCode ?: "unknown"
                        val endTime = System.currentTimeMillis()
                        val elapsedTime = endTime - _downloadStartTime
                        val remainingDelay = max(0L, MIN_DIALOG_DISPLAY_DURATION_MS - elapsedTime)
                        Timber.tag("LanguageViewModel")
                            .d("Download CANCELED for $languageCanceled. Elapsed: $elapsedTime ms, Delaying for: $remainingDelay ms")
                        delay(remainingDelay)

                        Timber.tag("LanguageViewModel").d("Download canceled for $languageCanceled")
                        val errorMessage = "Download canceled"
                        _downloadStatus.send(DownloadStatus.Error(errorMessage, languageCanceled))

                        if (_selectedUiLanguageCode.value == languageCanceled) {
                            _isCurrentlySelectedLanguageReady.value = false
                        }
                        resetDownloadState()
                    }
                }

                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    Timber.tag("LanguageViewModel")
                        .d("User confirmation required for download: $downloadId")
                    viewModelScope.launch {
                        val languageNeedingConfirm =
                            langCodeBeingHandled ?: currentlyDownloadingLanguageCode ?: "unknown"
                        val errorMessage = "Download requires user confirmation."
                        _downloadStatus.send(
                            DownloadStatus.Error(
                                errorMessage,
                                languageNeedingConfirm
                            )
                        )

                        if (_selectedUiLanguageCode.value == languageNeedingConfirm) {
                            _isCurrentlySelectedLanguageReady.value = false
                        }
                        resetDownloadState()
                    }
                }

                else -> {
                    Timber.tag("LanguageViewModel")
                        .d("Unhandled SplitInstallState: ${state.status()}")
                }
            }
        }
        splitInstallManager.registerListener(splitInstallListener!!)
    }

    private fun resetDownloadState() {
        currentlyDownloadingLanguageCode = null
        _isDownloadInProgress.value = false
        _downloadStartTime = 0L
        viewModelScope.launch {
            _downloadStatus.send(DownloadStatus.Idle)
        }
    }

    override fun onCleared() {
        super.onCleared()
        splitInstallListener?.let {
            splitInstallManager.unregisterListener(it)
        }
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            val availableLangs = languageRepository.getAvailableLanguages()
            _languages.value = availableLangs

            val initialSelectedCode =
                preferencesManager.getSelectedLanguageCode() ?: Language.SYSTEM_DEFAULT_CODE
            _selectedUiLanguageCode.value = initialSelectedCode

            _isCurrentlySelectedLanguageReady.value =
                isLanguageCodeInstalledInternal(initialSelectedCode)
            Timber.tag("LanguageViewModel")
                .d("Initial selected language: $initialSelectedCode. Is ready: ${_isCurrentlySelectedLanguageReady.value}")
        }
    }

    fun onLanguageSelected(languageCode: String) {
        if (_isDownloadInProgress.value) {
            if (currentlyDownloadingLanguageCode != languageCode) {
                Timber.tag("LanguageViewModel")
                    .d("Download already in progress for $currentlyDownloadingLanguageCode, ignoring selection of: $languageCode")
            }
            return
        }

        _selectedUiLanguageCode.value = languageCode

        val isSelectedLanguageAlreadyReady = isLanguageCodeInstalledInternal(languageCode)

        if (isSelectedLanguageAlreadyReady) {
            Timber.tag("LanguageViewModel")
                .d("Selected language ($languageCode) is already ready. Marking ready for save, clearing any old download status visuals.")
            _isCurrentlySelectedLanguageReady.value = true
            resetDownloadState()
        } else {
            Timber.tag("LanguageViewModel")
                .d("Selected language ($languageCode) is not installed. Marking NOT ready for save and initiating download.")
            _isCurrentlySelectedLanguageReady.value = false
            downloadLanguage(languageCode)
        }
        Timber.tag("LanguageViewModel")
            .d("onLanguageSelected - New selection: $languageCode, _isCurrentlySelectedLanguageReady: ${_isCurrentlySelectedLanguageReady.value}")
    }

    private fun downloadLanguage(languageCode: String) {
        viewModelScope.launch {
            currentlyDownloadingLanguageCode = languageCode
            _isDownloadInProgress.value = true
            _downloadStartTime = System.currentTimeMillis()

            _downloadStatus.send(DownloadStatus.Progress(0, 0, languageCode))

            val request = SplitInstallRequest.newBuilder()
                .addLanguage(Locale.forLanguageTag(languageCode))
                .build()

            splitInstallManager.startInstall(request)
                .addOnSuccessListener { sessionId ->
                    Timber.tag("LanguageViewModel")
                        .d("Language download request sent for $languageCode, session ID: $sessionId")
                }
                .addOnFailureListener { exception ->
                    viewModelScope.launch {
                        val endTime = System.currentTimeMillis()
                        val elapsedTime = endTime - _downloadStartTime
                        val remainingDelay = max(0L, MIN_DIALOG_DISPLAY_DURATION_MS - elapsedTime)
                        Timber.tag("LanguageViewModel")
                            .d("Immediate startInstall Failure for $languageCode. Delaying for: $remainingDelay ms")
                        delay(remainingDelay)

                        val errorMessage =
                            exception.message ?: "Unknown error starting download immediately."
                        _downloadStatus.send(DownloadStatus.Error(errorMessage, languageCode))
                        Timber.tag("LanguageViewModel")
                            .e("Failed to start language download for $languageCode: $errorMessage")

                        if (_selectedUiLanguageCode.value == languageCode) {
                            _isCurrentlySelectedLanguageReady.value = false
                        }
                        resetDownloadState()
                    }
                }
        }
    }

    private fun isLanguageCodeInstalledInternal(langCode: String): Boolean {
        if (langCode == Language.SYSTEM_DEFAULT_CODE) {
            return true
        }
        val installedByPlay = splitInstallManager.installedLanguages.contains(langCode)
        val baseBundledLanguages = listOf("en")
        val isBaseBundled = baseBundledLanguages.contains(langCode)

        return installedByPlay || isBaseBundled
    }

    fun onSaveClicked(isFromOnboarding: Boolean) {
        viewModelScope.launch {
            if (_isDownloadInProgress.value || !_isCurrentlySelectedLanguageReady.value) {
                Timber.tag("LanguageViewModel")
                    .w("Save clicked while not ready or download in progress. Ignoring.")
                return@launch
            }

            val newLangCode = _selectedUiLanguageCode.value ?: Language.SYSTEM_DEFAULT_CODE
            val currentAppLangCodeFromPrefs =
                preferencesManager.getSelectedLanguageCode() ?: Language.SYSTEM_DEFAULT_CODE

            if (newLangCode != currentAppLangCodeFromPrefs) {
                preferencesManager.saveSelectedLanguageCode(newLangCode)
                _navigateAction.emit(NavigateAction.RecreateActivity)
                Timber.tag("LanguageViewModel")
                    .d("Saved new language preference: $newLangCode. Emitting RecreateActivity.")
            } else {
                if (isFromOnboarding) {
                    _navigateAction.emit(NavigateAction.CompleteOnboarding)
                    Timber.tag("LanguageViewModel")
                        .d("Onboarding completed without language change. Emitting CompleteOnboarding to go to Main.")
                } else {
                    _navigateAction.emit(NavigateAction.PopStack)
                    Timber.tag("LanguageViewModel")
                        .d("Selected language is same as current ($newLangCode) from settings. Emitting PopStack.")
                }
            }
            resetDownloadState()
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigateAction.emit(NavigateAction.PopStack)
            Timber.tag("LanguageViewModel").d("Back button clicked. Emitting PopStack.")
        }
    }

    fun resetDownloadStatus() {
        resetDownloadState()

        val currentSelected = _selectedUiLanguageCode.value
        _isCurrentlySelectedLanguageReady.value = if (currentSelected != null) {
            isLanguageCodeInstalledInternal(currentSelected)
        } else {
            false
        }
        Timber.tag("LanguageViewModel")
            .d("Manual reset of download status (e.g., error dialog dismissed). Current UI selection: $currentSelected. Is ready: ${_isCurrentlySelectedLanguageReady.value}")
    }
}