package com.github.dsbezerra.cashflow.ui.categories

sealed interface CategoryListAction {
    data object Refresh : CategoryListAction
}
