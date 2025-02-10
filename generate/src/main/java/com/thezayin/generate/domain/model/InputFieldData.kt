package com.thezayin.generate.domain.model

data class InputFieldData(
    val label: String,
    val value: String,
    val onValueChange: (String) -> Unit,
    val placeholder: String = "",
    val isError: Boolean = false,
    val validation: (String) -> Boolean
)