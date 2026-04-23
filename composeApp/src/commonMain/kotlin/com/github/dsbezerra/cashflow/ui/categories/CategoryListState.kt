package com.github.dsbezerra.cashflow.ui.categories

import com.github.dsbezerra.cashflow.domain.model.Category

data class CategoryListState(
    val active: List<Category> = emptyList(),
    val archived: List<Category> = emptyList(),
    val isLoading: Boolean = true,
)
