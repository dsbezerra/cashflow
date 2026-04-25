package com.github.dsbezerra.cashflow.feature.category.list

sealed interface CategoryListAction {
    data object Refresh : CategoryListAction
}
