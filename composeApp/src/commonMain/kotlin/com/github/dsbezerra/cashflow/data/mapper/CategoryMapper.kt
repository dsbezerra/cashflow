package com.github.dsbezerra.cashflow.data.mapper

import com.github.dsbezerra.cashflow.db.Category as CategoryEntity
import com.github.dsbezerra.cashflow.domain.model.Category

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    type = type,
    icon = icon,
    color = color,
    parentId = parentId,
    isDefault = isDefault,
    isArchived = isArchived,
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    type = type,
    icon = icon,
    color = color,
    parentId = parentId,
    isDefault = isDefault,
    isArchived = isArchived,
)
