package com.github.dsbezerra.cashflow.ui.categories

sealed interface CategoryListEvent {
    data class ShowError(val message: String) : CategoryListEvent
}
