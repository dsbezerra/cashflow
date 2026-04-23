package com.github.dsbezerra.cashflow.ui.categories

sealed interface CategoryFormEvent {
    data object NavigateBack : CategoryFormEvent
    data class ShowError(val message: String) : CategoryFormEvent
}
