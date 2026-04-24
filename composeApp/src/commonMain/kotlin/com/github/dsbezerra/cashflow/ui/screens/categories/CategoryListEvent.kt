package com.github.dsbezerra.cashflow.ui.screens.categories

sealed interface CategoryListEvent {
    data class ShowError(val message: String) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryListEvent
}
