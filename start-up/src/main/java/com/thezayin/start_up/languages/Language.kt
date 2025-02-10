package com.thezayin.start_up.languages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
value class Locale(val value: String)

@Serializable
data class Language(
    @SerialName("language") val name: String,
    @SerialName("code") private val code: String
) {
    val locale
        get() = Locale(code)

    companion object {
        val default = Language("English", "en")
    }
}