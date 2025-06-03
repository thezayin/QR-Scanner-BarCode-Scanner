package com.thezayin.scanner.presentation.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.thezayin.scanner.presentation.result.component.ResultScreenContent
import org.koin.compose.koinInject

@Composable
fun ResultScreen(
    onNavigateUp: () -> Unit,
    navigateToPremium: () -> Unit,
    viewModel: ResultScreenViewModel = koinInject(),
) {
    val state = viewModel.state.collectAsState().value
    ResultScreenContent(
        state = state,
        viewModel = viewModel,
        onNavigateUp = onNavigateUp,
        navigateToPremium = navigateToPremium
    )
}