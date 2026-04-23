package com.github.dsbezerra.cashflow.ui.categories

import com.github.dsbezerra.cashflow.domain.model.CategoryType
import com.github.dsbezerra.cashflow.domain.model.DreClassification

sealed interface CategoryFormAction {
    data class NameChanged(val name: String) : CategoryFormAction
    data class TypeChanged(val type: CategoryType) : CategoryFormAction
    data class DreClassificationChanged(val dreClassification: DreClassification) : CategoryFormAction
    data class IconChanged(val icon: String) : CategoryFormAction
    data class ColorChanged(val color: String) : CategoryFormAction
    data class ParentChanged(val parentId: String?) : CategoryFormAction
    data class ArchivedChanged(val isArchived: Boolean) : CategoryFormAction
    data object Save : CategoryFormAction
    data object ConfirmDelete : CategoryFormAction
}
