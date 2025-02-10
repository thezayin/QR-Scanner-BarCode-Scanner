package com.thezayin.history.domain.model

data class CreateItem(
    val id: Long,
    val title: String,
    val content: String,
    val imageUri: String,
    val timestamp: Long
)