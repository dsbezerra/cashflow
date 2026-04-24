package com.github.dsbezerra.cashflow.ui.screens.categories

sealed interface CategoryListAction {
    data object Refresh : CategoryListAction
}
