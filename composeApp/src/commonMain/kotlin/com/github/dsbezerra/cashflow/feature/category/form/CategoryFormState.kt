package com.github.dsbezerra.cashflow.feature.category.form

import com.github.dsbezerra.cashflow.core.domain.model.Category
import com.github.dsbezerra.cashflow.core.domain.model.CategoryType
import com.github.dsbezerra.cashflow.core.domain.model.DreClassification

data class CategoryFormState(
    val categoryId: String? = null,
    val name: String = "",
    val nameError: String? = null,
    val type: CategoryType = CategoryType.EXPENSE,
    val dreClassification: DreClassification = DreClassification.EXPENSE,
    val icon: String = "category",
    val color: String = "#9E9E9E",
    val parentId: String? = null,
    val isArchived: Boolean = false,
    val isDefault: Boolean = false,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val availableParents: List<Category> = emptyList(),
)
