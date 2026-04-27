package com.github.dsbezerra.cashflow.core.data.mapper

import com.github.dsbezerra.cashflow.db.Category as CategoryEntity
import com.github.dsbezerra.cashflow.core.domain.model.Category

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    type = type,
    dreClassification = dreClassification,
    icon = icon,
    parentId = parentId,
    isDefault = isDefault,
    isArchived = isArchived,
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    type = type,
    dreClassification = dreClassification,
    icon = icon,
    parentId = parentId,
    isDefault = isDefault,
    isArchived = isArchived,
)
