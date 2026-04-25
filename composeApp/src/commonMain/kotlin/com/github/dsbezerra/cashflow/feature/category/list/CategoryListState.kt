package com.github.dsbezerra.cashflow.feature.category.list

import com.github.dsbezerra.cashflow.core.domain.model.Category

data class CategoryListState(
    val active: List<Category> = emptyList(),
    val archived: List<Category> = emptyList(),
    val isLoading: Boolean = true,
)
