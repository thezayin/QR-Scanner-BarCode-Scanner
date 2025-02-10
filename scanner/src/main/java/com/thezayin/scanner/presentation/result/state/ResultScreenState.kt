package com.thezayin.scanner.presentation.result.state

import com.thezayin.scanner.domain.model.ResultScreenItem

data class ResultScreenState(
    val scanItems: List<ResultScreenItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)