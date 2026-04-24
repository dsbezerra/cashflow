package com.github.dsbezerra.cashflow.ui.screens.categories

import com.github.dsbezerra.cashflow.domain.model.CategoryType
import com.github.dsbezerra.cashflow.domain.model.DreClassification

sealed interface CategoryFormAction {
    data class NameChanged(val name: String) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
    data class TypeChanged(val type: CategoryType) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
    data class DreClassificationChanged(val dreClassification: DreClassification) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
    data class IconChanged(val icon: String) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
    data class ColorChanged(val color: String) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
    data class ParentChanged(val parentId: String?) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
    data class ArchivedChanged(val isArchived: Boolean) :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
    data object Save : com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
    data object ConfirmDelete :
        com.github.dsbezerra.cashflow.ui.screens.categories.CategoryFormAction
}
