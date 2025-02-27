package com.thezayin.start_up.languages

import java.util.Locale

sealed interface LanguageState : State {

    data object Initialization : LanguageState

    data class Content(
        val selected: Locale,
        val languages: List<Language>,
        val selectedLang: Language
    ) : LanguageState
}

fun LanguageState.Content.copy(
    selected: Locale = this.selected,
    languages: List<Language> = this.languages,
    selectedLang: Language = this.selectedLang
): LanguageState.Content {
    return LanguageState.Content(
        selected, languages, selectedLang
    )
}