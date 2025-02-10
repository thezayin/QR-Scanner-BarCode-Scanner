package com.thezayin.history.domain.model

data class ScanItem(
    val id: Long,
    val imageUri: String,
    val scannedText: String,
    val type: String,
    val timestamp: Long,
    val isFavorite: Boolean,
    val description: String? = null
)