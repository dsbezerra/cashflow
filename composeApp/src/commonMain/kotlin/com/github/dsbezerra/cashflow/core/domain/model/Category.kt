package com.github.dsbezerra.cashflow.core.domain.model

data class Category(
    val id: String,
    val name: String,
    val type: CategoryType,
    val dreClassification: DreClassification,
    val icon: String,
    val parentId: String?,
    val isDefault: Boolean,
    val isArchived: Boolean,
)
