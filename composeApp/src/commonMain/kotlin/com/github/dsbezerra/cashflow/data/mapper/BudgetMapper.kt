package com.github.dsbezerra.cashflow.data.mapper

import com.github.dsbezerra.cashflow.db.Budget as BudgetEntity
import com.github.dsbezerra.cashflow.domain.model.Budget

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    categoryId = categoryId,
    amount = amount,
    period = period,
    startDate = startDate,
    isActive = isActive,
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    categoryId = categoryId,
    amount = amount,
    period = period,
    startDate = startDate,
    isActive = isActive,
)
