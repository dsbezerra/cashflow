package com.github.dsbezerra.cashflow.feature.category.form

sealed interface CategoryFormEvent {
    data object NavigateBack : CategoryFormEvent
    data class ShowError(val message: String) :
        CategoryFormEvent
}
