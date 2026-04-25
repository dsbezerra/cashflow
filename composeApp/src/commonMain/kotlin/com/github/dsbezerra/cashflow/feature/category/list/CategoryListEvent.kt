package com.github.dsbezerra.cashflow.feature.category.list

sealed interface CategoryListEvent {
    data class ShowError(val message: String) :
        CategoryListEvent
}
