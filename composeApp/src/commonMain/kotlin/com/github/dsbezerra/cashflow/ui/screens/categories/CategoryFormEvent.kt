package com.github.dsbezerra.cashflow.ui.screens.categories

sealed interface CategoryFormEvent {
    data object NavigateBack : com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormEvent
    data class ShowError(val message: String) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormEvent
}
