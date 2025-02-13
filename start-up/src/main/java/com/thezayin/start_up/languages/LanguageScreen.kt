package com.thezayin.start_up.languages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.thezayin.start_up.languages.component.LanguageScreenContent
import org.koin.compose.koinInject

@Composable
fun LanguageScreen(
    onLanguageSelection: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LanguageViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    LanguageScreenContent(
        state = state,
        viewModel = viewModel,
        onLanguageSelection = onLanguageSelection,
        onNavigateBack = onNavigateBack
    )
}

