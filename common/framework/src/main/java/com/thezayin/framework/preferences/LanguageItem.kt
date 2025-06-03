package com.thezayin.framework.preferences

data class Language(
    val code: String,
    val name: String,
    val flagEmoji: String?
) {
    companion object {
        const val SYSTEM_DEFAULT_CODE = "system_default"
    }
}