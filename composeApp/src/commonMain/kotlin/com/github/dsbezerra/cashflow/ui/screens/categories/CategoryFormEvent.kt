package com.github.dsbezerra.cashflow.ui.screens.categories

sealed interface CategoryFormEvent {
    data object NavigateBack : CategoryFormEvent
    data class ShowError(val message: String) :
        CategoryFormEvent
}
