package com.thezayin.start_up.languages.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.thezayin.framework.components.AdLoadingDialog
import com.thezayin.framework.components.ComposableLifecycle
import com.thezayin.framework.components.GoogleNativeSimpleAd
import com.thezayin.start_up.languages.LanguageViewModel
import com.thezayin.start_up.languages.state.LanguageState
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreenContent(
    state: LanguageState,
    viewModel: LanguageViewModel,
    onLanguageSelection: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Move the coroutine logic and lifecycle handling here
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val showLoadingAd = remember { mutableStateOf(false) }

    if (showLoadingAd.value) {
        AdLoadingDialog()
    }

    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                scope.coroutineContext.cancelChildren()
                scope.launch {
                    while (isActive) {
                        viewModel.getNativeAd(context)
                        delay(20_000L)
                    }
                }
            }

            else -> Unit
        }
    }

    // The Scaffold and content UI
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = { Text(text = "Select Language") }
                )
            }
        },
        bottomBar = {
            GoogleNativeSimpleAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(150.dp),
                nativeAd = viewModel.nativeAd.value
            )
        },
        content = { paddingValues ->
            when (state) {
                is LanguageState.Initialization -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LanguageState.Content -> {
                    val content = state
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items(content.languages) { language ->
                            LanguageListItem(
                                language = language,
                                isSelected = language == content.selectedLang,
                                onClick = {
                                    viewModel.onLanguageSelected(language)
                                    onLanguageSelection()
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
